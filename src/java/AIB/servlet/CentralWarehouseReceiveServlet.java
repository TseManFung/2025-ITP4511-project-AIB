package AIB.servlet;

import AIB.Bean.WarehouseStockChangeBean;
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

@WebServlet("/centralWarehouseReceiveServlet")
public class CentralWarehouseReceiveServlet extends HttpServlet {
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
        List<WarehouseStockChangeBean> pendingTransfers = new ArrayList<>();
        List<WarehouseStockChangeBean> transactionHistory = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            // Verify warehouse type
            PreparedStatement stmt = conn.prepareStatement("SELECT type FROM warehouse WHERE id = ?");
            stmt.setLong(1, warehouseId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || !"C".equals(rs.getString("type"))) {
                response.sendRedirect("login.jsp?error=access_denied");
                return;
            }

            // Get pending transfers
            stmt = conn.prepareStatement(
                "SELECT wsc.*, wscd.fruitid, wscd.num " +
                "FROM warehouseStockChange wsc " +
                "JOIN warehouseStockChangeDetail wscd ON wsc.id = wscd.warehouseStockChangeid " +
                "WHERE wsc.destinationWarehouseid = ? AND wsc.state = 'A' AND wscd.state = 'C'"
            );
            stmt.setLong(1, warehouseId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                WarehouseStockChangeBean transfer = new WarehouseStockChangeBean();
                transfer.setId(rs.getLong("id"));
                transfer.setDeliveryStartTime(rs.getTimestamp("deliveryStartTime"));
                transfer.setSourceWarehouseId(rs.getLong("sourceWarehouseid"));
                transfer.setDestinationWarehouseId(rs.getLong("destinationWarehouseid"));
                transfer.setState(rs.getString("state"));
                transfer.setFruitId(rs.getLong("fruitid"));
                transfer.setNum(rs.getInt("num"));
                pendingTransfers.add(transfer);
            }

            // Get transaction history (states C, A, F)
            stmt = conn.prepareStatement(
                "SELECT wsc.*, wscd.fruitid, wscd.num, wscd.state AS detail_state " +
                "FROM warehouseStockChange wsc " +
                "JOIN warehouseStockChangeDetail wscd ON wsc.id = wscd.warehouseStockChangeid " +
                "WHERE wsc.destinationWarehouseid = ? AND wsc.state IN ('C', 'A', 'F')"
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
                transfer.setDetailState(rs.getString("detail_state"));
                transactionHistory.add(transfer);
            }

            // Get warehouse and fruit names for display
            List<String> warehouses = new ArrayList<>();
            List<String> fruits = new ArrayList<>();
            stmt = conn.prepareStatement("SELECT id, name FROM warehouse");
            rs = stmt.executeQuery();
            while (rs.next()) {
                warehouses.add(rs.getLong("id") + ":" + rs.getString("name"));
            }
            stmt = conn.prepareStatement("SELECT id, name FROM fruit");
            rs = stmt.executeQuery();
            while (rs.next()) {
                fruits.add(rs.getLong("id") + ":" + rs.getString("name"));
            }

            request.setAttribute("warehouses", warehouses);
            request.setAttribute("fruits", fruits);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }

        request.setAttribute("pendingTransfers", pendingTransfers);
        request.setAttribute("transactionHistory", transactionHistory);
        request.getRequestDispatcher("/Warehouse/centralWarehouseReceive.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"W".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        Long warehouseId = (Long) session.getAttribute("warehouseId");
        String[] selectedItems = request.getParameterValues("selectedItems");
        String action = request.getParameter("action");

        if (selectedItems == null || selectedItems.length == 0) {
            request.setAttribute("error", "No items selected");
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
                response.sendRedirect("login.jsp?error=access_denied");
                return;
            }

            // Process each selected item
            for (String item : selectedItems) {
                String[] parts = item.split(":");
                Long transferId = Long.parseLong(parts[0]);
                Long fruitId = Long.parseLong(parts[1]);

                if ("receive".equals(action)) {
                    // Update detail state to finished
                    stmt = conn.prepareStatement(
                        "UPDATE warehouseStockChangeDetail SET state = 'F' WHERE warehouseStockChangeid = ? AND fruitid = ?"
                    );
                    stmt.setLong(1, transferId);
                    stmt.setLong(2, fruitId);
                    stmt.executeUpdate();

                    // Update central warehouse stock
                    stmt = conn.prepareStatement(
                        "INSERT INTO warehouseStock (warehouseid, fruitid, num) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE num = num + ?"
                    );
                    stmt.setLong(1, warehouseId);
                    stmt.setLong(2, fruitId);
                    stmt.setInt(3, getTransferNum(conn, transferId, fruitId));
                    stmt.setInt(4, getTransferNum(conn, transferId, fruitId));
                    stmt.executeUpdate();

                    // Check if all details are finished for this transfer
                    stmt = conn.prepareStatement(
                        "SELECT COUNT(*) AS total, SUM(CASE WHEN state = 'F' THEN 1 ELSE 0 END) AS finished " +
                        "FROM warehouseStockChangeDetail WHERE warehouseStockChangeid = ?"
                    );
                    stmt.setLong(1, transferId);
                    rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt("total") == rs.getInt("finished")) {
                        // Update transfer status to finished
                        stmt = conn.prepareStatement(
                            "UPDATE warehouseStockChange SET state = 'F', deliveryEndTime = NOW() WHERE id = ?"
                        );
                        stmt.setLong(1, transferId);
                        stmt.executeUpdate();
                    }
                } else if ("reject".equals(action)) {
                    // Update detail state to rejected
                    stmt = conn.prepareStatement(
                        "UPDATE warehouseStockChangeDetail SET state = 'R' WHERE warehouseStockChangeid = ? AND fruitid = ?"
                    );
                    stmt.setLong(1, transferId);
                    stmt.setLong(2, fruitId);
                    stmt.executeUpdate();

                    // Return stock to source warehouse
                    stmt = conn.prepareStatement(
                        "INSERT INTO warehouseStock (warehouseid, fruitid, num) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE num = num + ?"
                    );
                    stmt.setLong(1, getSourceWarehouseId(conn, transferId));
                    stmt.setLong(2, fruitId);
                    stmt.setInt(3, getTransferNum(conn, transferId, fruitId));
                    stmt.setInt(4, getTransferNum(conn, transferId, fruitId));
                    stmt.executeUpdate();

                    // Update transfer status to rejected if all details are rejected
                    stmt = conn.prepareStatement(
                        "SELECT COUNT(*) AS total, SUM(CASE WHEN state = 'R' THEN 1 ELSE 0 END) AS rejected " +
                        "FROM warehouseStockChangeDetail WHERE warehouseStockChangeid = ?"
                    );
                    stmt.setLong(1, transferId);
                    rs = stmt.executeQuery();
                    if (rs.next() && rs.getInt("total") == rs.getInt("rejected")) {
                        stmt = conn.prepareStatement(
                            "UPDATE warehouseStockChange SET state = 'R' WHERE id = ?"
                        );
                        stmt.setLong(1, transferId);
                        stmt.executeUpdate();
                    }
                }
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }

        doGet(request, response);
    }

    private Integer getTransferNum(Connection conn, Long transferId, Long fruitId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT num FROM warehouseStockChangeDetail WHERE warehouseStockChangeid = ? AND fruitid = ?"
        );
        stmt.setLong(1, transferId);
        stmt.setLong(2, fruitId);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt("num");
    }

    private Long getSourceWarehouseId(Connection conn, Long transferId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT sourceWarehouseid FROM warehouseStockChange WHERE id = ?"
        );
        stmt.setLong(1, transferId);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getLong("sourceWarehouseid");
    }
}