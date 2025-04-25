package AIB.servlet;

import AIB.Bean.ReserveBean;
import AIB.db.ITP4511_DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/acceptReserveListServlet")
public class AcceptReserveListServlet extends HttpServlet {
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

        Long warehouseId = (Long) session.getAttribute("warehouseId");
        if (warehouseId == null) {
            response.sendRedirect("login.jsp?error=missing_warehouse_id");
            return;
        }

        List<ReserveBean> reserves = new ArrayList<>();
        List<String> shops = new ArrayList<>();
        List<String> fruits = new ArrayList<>();
        Long countryId = null;

        try (Connection conn = db.getConnection()) {
            // Verify warehouse type and get countryid
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT type, countryid FROM warehouse WHERE id = ?"
            );
            stmt.setLong(1, warehouseId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || !"C".equals(rs.getString("type"))) {
                response.sendRedirect("login.jsp?error=invalid_warehouse_type");
                return;
            }
            countryId = rs.getLong("countryid");

            // Get pending reserves for shops in the same country
            stmt = conn.prepareStatement(
                "SELECT r.*, rd.fruitid, rd.num " +
                "FROM reserve r " +
                "JOIN reserveDetail rd ON r.id = rd.reserveid " +
                "JOIN shop s ON r.Shopid = s.id " +
                "JOIN city ci ON s.cityid = ci.id " +
                "WHERE r.state = 'C' AND ci.countryid = ? " +
                "ORDER BY r.id, rd.fruitid"
            );
            stmt.setLong(1, countryId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                ReserveBean reserve = new ReserveBean();
                reserve.setId(rs.getLong("id"));
                reserve.setShopId(rs.getLong("Shopid"));
                reserve.setReserveDT(rs.getTimestamp("reserveDT"));
                reserve.setState(rs.getString("state"));
                reserve.setFruitId(rs.getLong("fruitid"));
                reserve.setNum(rs.getInt("num"));
                reserves.add(reserve);
            }

            // Get shop names for display (same country only)
            stmt = conn.prepareStatement(
                "SELECT s.id, s.name " +
                "FROM shop s " +
                "JOIN city ci ON s.cityid = ci.id " +
                "WHERE ci.countryid = ?"
            );
            stmt.setLong(1, countryId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                shops.add(rs.getLong("id") + ":" + rs.getString("name"));
            }

            // Get fruit names for display
            stmt = conn.prepareStatement("SELECT id, name FROM fruit");
            rs = stmt.executeQuery();
            while (rs.next()) {
                fruits.add(rs.getLong("id") + ":" + rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error, please contact administrator");
        }

        request.setAttribute("reserves", reserves);
        request.setAttribute("shops", shops);
        request.setAttribute("fruits", fruits);
        request.getRequestDispatcher("/Warehouse/acceptReserveList.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"W".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        Long warehouseId = (Long) session.getAttribute("warehouseId");
        if (warehouseId == null) {
            response.sendRedirect("login.jsp?error=missing_warehouse_id");
            return;
        }

        String[] selectedReserves = request.getParameterValues("selectedReserves");
        String action = request.getParameter("action");

        if (selectedReserves == null || selectedReserves.length == 0) {
            request.setAttribute("error", "No reservations selected");
            doGet(request, response);
            return;
        }

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            // Verify warehouse type
            PreparedStatement stmt = conn.prepareStatement("SELECT type FROM warehouse WHERE id = ?");
            stmt.setLong(1, warehouseId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || !"C".equals(rs.getString("type"))) {
                conn.rollback();
                response.sendRedirect("login.jsp?error=invalid_warehouse_type");
                return;
            }

            for (String reserveIdStr : selectedReserves) {
                Long reserveId = Long.parseLong(reserveIdStr);

                if ("approve".equals(action)) {
                    // Check stock availability for all fruits in the reservation
                    stmt = conn.prepareStatement(
                        "SELECT rd.fruitid, rd.num, COALESCE(ws.num, 0) AS stock " +
                        "FROM reserveDetail rd " +
                        "LEFT JOIN warehouseStock ws ON ws.fruitid = rd.fruitid AND ws.warehouseid = ? " +
                        "WHERE rd.reserveid = ?"
                    );
                    stmt.setLong(1, warehouseId);
                    stmt.setLong(2, reserveId);
                    rs = stmt.executeQuery();
                    boolean sufficientStock = true;
                    List<Long> fruitIds = new ArrayList<>();
                    List<Integer> quantities = new ArrayList<>();

                    while (rs.next()) {
                        int requiredNum = rs.getInt("num");
                        int availableStock = rs.getInt("stock");
                        if (availableStock < requiredNum) {
                            sufficientStock = false;
                            break;
                        }
                        fruitIds.add(rs.getLong("fruitid"));
                        quantities.add(requiredNum);
                    }

                    if (!sufficientStock) {
                        conn.rollback();
                        request.setAttribute("error", "Insufficient stock for reservation ID " + reserveId);
                        doGet(request, response);
                        return;
                    }

                    // Update reserve status
                    stmt = conn.prepareStatement(
                        "UPDATE reserve SET state = 'A' WHERE id = ?"
                    );
                    stmt.setLong(1, reserveId);
                    stmt.executeUpdate();

                    // Update warehouse stock for each fruit
                    stmt = conn.prepareStatement(
                        "UPDATE warehouseStock SET num = num - ? WHERE warehouseid = ? AND fruitid = ?"
                    );
                    for (int i = 0; i < fruitIds.size(); i++) {
                        stmt.setInt(1, quantities.get(i));
                        stmt.setLong(2, warehouseId);
                        stmt.setLong(3, fruitIds.get(i));
                        stmt.addBatch();
                    }
                    stmt.executeBatch();

                    // Update shop stock for each fruit
                    stmt = conn.prepareStatement(
                        "SELECT Shopid FROM reserve WHERE id = ?"
                    );
                    stmt.setLong(1, reserveId);
                    rs = stmt.executeQuery();
                    rs.next();
                    Long shopId = rs.getLong("Shopid");

                    stmt = conn.prepareStatement(
                        "INSERT INTO shopStock (shopid, fruitid, num) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE num = num + ?"
                    );
                    for (int i = 0; i < fruitIds.size(); i++) {
                        stmt.setLong(1, shopId);
                        stmt.setLong(2, fruitIds.get(i));
                        stmt.setInt(3, quantities.get(i));
                        stmt.setInt(4, quantities.get(i));
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                } else if ("reject".equals(action)) {
                    // Update reserve status
                    stmt = conn.prepareStatement(
                        "UPDATE reserve SET state = 'R' WHERE id = ?"
                    );
                    stmt.setLong(1, reserveId);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error, please contact administrator");
        }

        doGet(request, response);
    }
}