package AIB.servlet;

import AIB.Bean.WarehouseStockChangeBean;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/sourceWarehouseStockChangeServlet")
public class SourceWarehouseStockChangeServlet extends HttpServlet {

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
        List<WarehouseStockChangeBean> transfers = new ArrayList<>();
        List<String> centralWarehouses = new ArrayList<>();
        List<String> fruits = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            // Verify warehouse type
            PreparedStatement stmt = conn.prepareStatement("SELECT type FROM warehouse WHERE id = ?");
            stmt.setLong(1, warehouseId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || !"S".equals(rs.getString("type"))) {
                response.sendRedirect("login.jsp?error=access_denied");
                return;
            }

            // Get transfer history
            stmt = conn.prepareStatement(
                    "SELECT wsc.*, wscd.fruitid, wscd.num "
                    + "FROM warehouseStockChange wsc "
                    + "JOIN warehouseStockChangeDetail wscd ON wsc.id = wscd.warehouseStockChangeid "
                    + "WHERE wsc.sourceWarehouseid = ?"
            );
            stmt.setLong(1, warehouseId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                WarehouseStockChangeBean transfer = new WarehouseStockChangeBean();
                transfer.setId(rs.getLong("id"));
                transfer.setDeliveryStartTime(rs.getTimestamp("deliveryStartTime"));
                transfer.setDeliveryEndTime(rs.getTimestamp("deliveryEndTime"));
                transfer.setSourceWarehouseId(rs.getLong("sourceWarehouseid"));
                transfer.setDestinationWarehouseId(rs.getLong("destinationWarehouseid"));
                transfer.setState(rs.getString("state"));
                transfer.setFruitId(rs.getLong("fruitid"));
                transfer.setNum(rs.getInt("num"));
                transfers.add(transfer);
            }

            // Get available central warehouses in the same country
            stmt = conn.prepareStatement(
                    "SELECT id, name FROM warehouse WHERE type = 'C' AND countryid = "
                    + "(SELECT countryid FROM warehouse WHERE id = ?)"
            );
            stmt.setLong(1, warehouseId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                centralWarehouses.add(rs.getLong("id") + ":" + rs.getString("name"));
            }

            // Get available fruits in source warehouse
            stmt = conn.prepareStatement(
                    "SELECT f.id, f.name FROM fruit f "
                    + "JOIN warehouseStock ws ON f.id = ws.fruitid "
                    + "WHERE ws.warehouseid = ? AND ws.num > 0"
            );
            stmt.setLong(1, warehouseId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                fruits.add(rs.getLong("id") + ":" + rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }

        request.setAttribute("transfers", transfers);
        request.setAttribute("centralWarehouses", centralWarehouses);
        request.setAttribute("fruits", fruits);
        request.getRequestDispatcher("/Warehouse/sourceWarehouseStockChange.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"W".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        Long warehouseId = (Long) session.getAttribute("warehouseId");
        Long destinationWarehouseId = Long.parseLong(request.getParameter("destinationWarehouseId"));
        Long fruitId = Long.parseLong(request.getParameter("fruitId"));
        Integer num = Integer.parseInt(request.getParameter("num"));

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            // Verify warehouse type and stock availability
            PreparedStatement stmt = conn.prepareStatement("SELECT type FROM warehouse WHERE id = ?");
            stmt.setLong(1, warehouseId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || !"S".equals(rs.getString("type"))) {
                conn.rollback();
                response.sendRedirect("login.jsp?error=access_denied");
                return;
            }

            stmt = conn.prepareStatement("SELECT num FROM warehouseStock WHERE warehouseid = ? AND fruitid = ?");
            stmt.setLong(1, warehouseId);
            stmt.setLong(2, fruitId);
            rs = stmt.executeQuery();
            if (!rs.next() || rs.getInt("num") < num) {
                conn.rollback();
                request.setAttribute("error", "Insufficient stock");
                doGet(request, response);
                return;
            }

            // Create new stock change record
    
            stmt = conn.prepareStatement(
                    "INSERT INTO warehouseStockChange (id, sourceWarehouseid, destinationWarehouseid, state) "
                    + "VALUES (?, ?, ?, 'A')", 
                    Statement.RETURN_GENERATED_KEYS
            );

            Long id = SnowflakeSingleton.getInstance().nextId();
            stmt.setLong(1, id);                 
            stmt.setLong(2, warehouseId);     
            stmt.setLong(3, destinationWarehouseId); 
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            rs.next();
            Long changeId = id;

            // Insert detail
            stmt = conn.prepareStatement(
                    "INSERT INTO warehouseStockChangeDetail (warehouseStockChangeid, fruitid, num) VALUES (?, ?, ?)"
            );
            stmt.setLong(1, changeId);
            stmt.setLong(2, fruitId);
            stmt.setInt(3, num);
            stmt.executeUpdate();

            // Update source warehouse stock
            stmt = conn.prepareStatement(
                    "UPDATE warehouseStock SET num = num - ? WHERE warehouseid = ? AND fruitid = ?"
            );
            stmt.setInt(1, num);
            stmt.setLong(2, warehouseId);
            stmt.setLong(3, fruitId);
            stmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }

        doGet(request, response);
    }
}
