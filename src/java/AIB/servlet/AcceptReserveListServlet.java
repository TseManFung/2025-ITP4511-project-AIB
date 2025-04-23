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

        try (Connection conn = db.getConnection()) {
            // Verify warehouse type
            PreparedStatement stmt = conn.prepareStatement("SELECT type FROM warehouse WHERE id = ?");
            stmt.setLong(1, warehouseId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || !"C".equals(rs.getString("type"))) {
                response.sendRedirect("login.jsp?error=invalid_warehouse_type");
                return;
            }

            // Get pending reserves
            stmt = conn.prepareStatement(
                "SELECT r.*, rd.fruitid, rd.num " +
                "FROM reserve r " +
                "JOIN reserveDetail rd ON r.id = rd.reserveid " +
                "WHERE r.state = 'C'"
            );
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

            // Get shop names for display
            stmt = conn.prepareStatement("SELECT id, name FROM shop");
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
            request.setAttribute("error", "資料庫錯誤，請聯繫管理員");
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

        Long reserveId = Long.parseLong(request.getParameter("reserveId"));
        String action = request.getParameter("action");

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

            if ("approve".equals(action)) {
                // Check stock availability
                stmt = conn.prepareStatement(
                    "SELECT rd.num, rd.fruitid FROM reserveDetail rd WHERE rd.reserveid = ?"
                );
                stmt.setLong(1, reserveId);
                rs = stmt.executeQuery();
                rs.next();
                Long fruitId = rs.getLong("fruitid");
                int num = rs.getInt("num");

                stmt = conn.prepareStatement(
                    "SELECT num FROM warehouseStock WHERE warehouseid = ? AND fruitid = ?"
                );
                stmt.setLong(1, warehouseId);
                stmt.setLong(2, fruitId);
                rs = stmt.executeQuery();
                if (!rs.next() || rs.getInt("num") < num) {
                    conn.rollback();
                    request.setAttribute("error", "庫存不足，無法批准預訂");
                    doGet(request, response);
                    return;
                }

                // Update reserve status
                stmt = conn.prepareStatement(
                    "UPDATE reserve SET state = 'A' WHERE id = ?"
                );
                stmt.setLong(1, reserveId);
                stmt.executeUpdate();

                // Update warehouse stock
                stmt = conn.prepareStatement(
                    "UPDATE warehouseStock SET num = num - ? WHERE warehouseid = ? AND fruitid = ?"
                );
                stmt.setInt(1, num);
                stmt.setLong(2, warehouseId);
                stmt.setLong(3, fruitId);
                stmt.executeUpdate();
            } else if ("reject".equals(action)) {
                // Update reserve status
                stmt = conn.prepareStatement(
                    "UPDATE reserve SET state = 'R' WHERE id = ?"
                );
                stmt.setLong(1, reserveId);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "資料庫錯誤，請聯繫管理員");
        }

        doGet(request, response);
    }
}