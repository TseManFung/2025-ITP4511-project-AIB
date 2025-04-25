package AIB.servlet;

import AIB.algorithm.SnowflakeSingleton;
import AIB.db.ITP4511_DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/sourceWarehouseSupplyServlet")
public class SourceWarehouseSupplyServlet extends HttpServlet {

    private ITP4511_DB db;

    @Override
    public void init() {
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        db = new ITP4511_DB(dbUrl, dbUser, dbPassword);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"W".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        Long sourceWarehouseId = (Long) session.getAttribute("warehouseId");
        try {
            Long sourceCountryId = getSourceCountryId(sourceWarehouseId);
            Map<Long, Map<Long, Integer>> demandByCountry = getSupplyDemand(sourceCountryId);
            Map<Long, Integer> availableStock = getAvailableStock(sourceWarehouseId);
            Map<Long, String> fruitNames = getFruitNames();
            Map<Long, String> countryNames = getCountryNames();

            request.setAttribute("demandByCountry", demandByCountry);
            request.setAttribute("availableStock", availableStock);
            request.setAttribute("fruitNames", fruitNames);
            request.setAttribute("countryNames", countryNames);
            request.getRequestDispatcher("/Warehouse/sourceWarehouseSupply.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/Warehouse/sourceWarehouseSupply.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"W".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        Long sourceWarehouseId = (Long) session.getAttribute("warehouseId");
        boolean acceptAll = "true".equals(request.getParameter("acceptAll"));

        try {
            Long sourceCountryId = getSourceCountryId(sourceWarehouseId);
            Map<Long, Map<Long, Integer>> demandByCountry = getSupplyDemand(sourceCountryId);
            Map<Long, Integer> availableStock = getAvailableStock(sourceWarehouseId);

            if (acceptAll) {
                // Process all countries' demands
                boolean allSufficient = true;
                for (Map.Entry<Long, Map<Long, Integer>> countryEntry : demandByCountry.entrySet()) {
                    Long targetCountryId = countryEntry.getKey();
                    Map<Long, Integer> demand = countryEntry.getValue();
                    for (Map.Entry<Long, Integer> entry : demand.entrySet()) {
                        Long fruitId = entry.getKey();
                        Integer needed = entry.getValue();
                        Integer available = availableStock.getOrDefault(fruitId, 0);
                        if (available < needed) {
                            allSufficient = false;
                            break;
                        }
                    }
                    if (!allSufficient) {
                        break;
                    }
                }

                if (allSufficient) {
                    for (Map.Entry<Long, Map<Long, Integer>> countryEntry : demandByCountry.entrySet()) {
                        Long targetCountryId = countryEntry.getKey();
                        Long centralWarehouseId = getCentralWarehouseId(targetCountryId);
                        Map<Long, Integer> demand = countryEntry.getValue();
                        createSupplyRecord(sourceWarehouseId, centralWarehouseId, demand);
                    }
                    response.sendRedirect("sourceWarehouseSupplyServlet?success=true");
                } else {
                    request.setAttribute("error", "Insufficient stock to fulfill all supply requests.");
                    doGet(request, response);
                }
            } else {
                // Process single country demand
                Long targetCountryId = Long.parseLong(request.getParameter("targetCountryId"));
                Long centralWarehouseId = getCentralWarehouseId(targetCountryId);
                Map<Long, Integer> demand = demandByCountry.get(targetCountryId);

                boolean isSufficient = true;
                for (Map.Entry<Long, Integer> entry : demand.entrySet()) {
                    Long fruitId = entry.getKey();
                    Integer needed = entry.getValue();
                    Integer available = availableStock.getOrDefault(fruitId, 0);
                    if (available < needed) {
                        isSufficient = false;
                        break;
                    }
                }

                if (isSufficient) {
                    createSupplyRecord(sourceWarehouseId, centralWarehouseId, demand);
                    response.sendRedirect("sourceWarehouseSupplyServlet?success=true");
                } else {
                    request.setAttribute("error", "Insufficient stock to fulfill the supply request for the selected country.");
                    doGet(request, response);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            doGet(request, response);
        }
    }

    private Long getSourceCountryId(Long sourceWarehouseId) throws SQLException {
        String sql = "SELECT countryid FROM warehouse WHERE id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, sourceWarehouseId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getLong("countryid") : null;
        }
    }

    private Long getCentralWarehouseId(Long countryId) throws SQLException {
        String sql = "SELECT id FROM warehouse WHERE countryid = ? AND type = 'C'";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, countryId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getLong("id") : null;
        }
    }

    private Map<Long, Map<Long, Integer>> getSupplyDemand(Long sourceCountryId) throws SQLException {
        Map<Long, Map<Long, Integer>> demandByCountry = new HashMap<>();
        String sql = "SELECT c.countryid, rd.fruitid, SUM(rd.num) AS total_needed " +
                     "FROM reserve r " +
                     "JOIN reserveDetail rd ON r.id = rd.reserveid " +
                     "JOIN shop s ON r.Shopid = s.id " +
                     "JOIN city c ON s.cityid = c.id " +
                     "JOIN fruit f ON rd.fruitid = f.id " +
                     "WHERE r.state = 'C' AND f.sourceCountryid = ? " +
                     "GROUP BY c.countryid, rd.fruitid";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, sourceCountryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long countryId = rs.getLong("countryid");
                Long fruitId = rs.getLong("fruitid");
                Integer totalNeeded = rs.getInt("total_needed");
                demandByCountry.computeIfAbsent(countryId, k -> new LinkedHashMap<>()).put(fruitId, totalNeeded);
            }
        }
        return demandByCountry;
    }

    private Map<Long, Integer> getAvailableStock(Long sourceWarehouseId) throws SQLException {
        Map<Long, Integer> availableStock = new LinkedHashMap<>();
        String sql = "SELECT ws.fruitid, (ws.num - COALESCE(SUM(wscd.num), 0)) AS available_num "
                + "FROM warehouseStock ws "
                + "LEFT JOIN warehouseStockChange wsc ON ws.warehouseid = wsc.sourceWarehouseid AND wsc.state IN ('C', 'A') "
                + "LEFT JOIN warehouseStockChangeDetail wscd ON wsc.id = wscd.warehouseStockChangeid AND ws.fruitid = wscd.fruitid "
                + "WHERE ws.warehouseid = ? GROUP BY ws.fruitid, ws.num";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, sourceWarehouseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                availableStock.put(rs.getLong("fruitid"), rs.getInt("available_num"));
            }
        }
        return availableStock;
    }

    private Map<Long, String> getFruitNames() throws SQLException {
        Map<Long, String> fruitNames = new LinkedHashMap<>();
        String sql = "SELECT id, name FROM fruit";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                fruitNames.put(rs.getLong("id"), rs.getString("name"));
            }
        }
        return fruitNames;
    }

    private Map<Long, String> getCountryNames() throws SQLException {
        Map<Long, String> countryNames = new LinkedHashMap<>();
        String sql = "SELECT id, name FROM country";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                countryNames.put(rs.getLong("id"), rs.getString("name"));
            }
        }
        return countryNames;
    }

    private void createSupplyRecord(Long sourceWarehouseId, Long centralWarehouseId, Map<Long, Integer> demand) throws SQLException {
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);
            long changeId = SnowflakeSingleton.getInstance().nextId();
            String changeSql = "INSERT INTO warehouseStockChange (id, sourceWarehouseid, destinationWarehouseid, state) VALUES (?, ?, ?, 'C')";
            try (PreparedStatement stmt = conn.prepareStatement(changeSql)) {
                stmt.setLong(1, changeId);
                stmt.setLong(2, sourceWarehouseId);
                stmt.setLong(3, centralWarehouseId);
                stmt.executeUpdate();
            }

            String detailSql = "INSERT INTO warehouseStockChangeDetail (warehouseStockChangeid, fruitid, num) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(detailSql)) {
                for (Map.Entry<Long, Integer> entry : demand.entrySet()) {
                    stmt.setLong(1, changeId);
                    stmt.setLong(2, entry.getKey());
                    stmt.setInt(3, entry.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.close();
        }
    }
}