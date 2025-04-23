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
                "WHERE wsc.destinationWarehouseid = ? AND wsc.state = 'A'"
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

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }

        request.setAttribute("pendingTransfers", pendingTransfers);
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
        Long transferId = Long.parseLong(request.getParameter("transferId"));
        String action = request.getParameter("action");

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

            if ("receive".equals(action)) {
                // Update transfer status to finished
                stmt = conn.prepareStatement(
                    "UPDATE warehouseStockChange SET state = 'F', deliveryEndTime = NOW() WHERE id = ?"
                );
                stmt.setLong(1, transferId);
                stmt.executeUpdate();

                // Update central warehouse stock
                stmt = conn.prepareStatement(
                    "INSERT INTO warehouseStock (warehouseid, fruitid, num) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE num = num + ?"
                );
                stmt.setLong(1, warehouseId);
                stmt.setLong(2, getFruitId(conn, transferId));
                stmt.setInt(3, getTransferNum(conn, transferId));
                stmt.setInt(4, getTransferNum(conn, transferId));
                stmt.executeUpdate();
            } else if ("reject".equals(action)) {
                // Update transfer status to rejected
                stmt = conn.prepareStatement(
                    "UPDATE warehouseStockChange SET state = 'R' WHERE id = ?"
                );
                stmt.setLong(1, transferId);
                stmt.executeUpdate();

                // Return stock to source warehouse
                stmt = conn.prepareStatement(
                    "UPDATE warehouseStock SET num = num + ? WHERE warehouseid = ? AND fruitid = ?"
                );
                stmt.setInt(1, getTransferNum(conn, transferId));
                stmt.setLong(2, getSourceWarehouseId(conn, transferId));
                stmt.setLong(3, getFruitId(conn, transferId));
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }

        doGet(request, response);
    }

    private Long getFruitId(Connection conn, Long transferId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT fruitid FROM warehouseStockChangeDetail WHERE warehouseStockChangeid = ?"
        );
        stmt.setLong(1, transferId);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getLong("fruitid");
    }

    private Integer getTransferNum(Connection conn, Long transferId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT num FROM warehouseStockChangeDetail WHERE warehouseStockChangeid = ?"
        );
        stmt.setLong(1, transferId);
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