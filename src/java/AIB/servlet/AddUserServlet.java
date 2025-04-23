package AIB.servlet;

import AIB.Bean.UserBean;
import AIB.db.ITP4511_DB;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.URLEncoder;

@WebServlet("/addUserServlet")
public class AddUserServlet extends HttpServlet {

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
        if (session == null || !"S".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        try (Connection conn = db.getConnection()) {
            Map<Long, String> shops = getReferenceData(conn, "shop");
            Map<Long, String> warehouses = getReferenceData(conn, "warehouse");
            
            request.setAttribute("shops", shops);
            request.setAttribute("warehouses", warehouses);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }
        
        request.getRequestDispatcher("/Manager/addUser.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"S".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        String loginName = request.getParameter("loginName");
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        char type = request.getParameter("type").charAt(0);
        Long shopId = parseLong(request.getParameter("shopId"));
        Long warehouseId = parseLong(request.getParameter("warehouseId"));

        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            // Auto-generate login name if empty
            if (loginName == null || loginName.trim().isEmpty()) {
                loginName = generateNewLoginName(conn, type);
            }

            // Validate required fields
            if (type == 'B' && shopId == null) {
                throw new SQLException("Shop selection required for Bakery Staff");
            }
            if (type == 'W' && warehouseId == null) {
                throw new SQLException("Warehouse selection required for Warehouse Staff");
            }

            // Create user
            String sql = "INSERT INTO user (loginName, name, password, type, shopid, warehouseid) "
                        + "VALUES (?, ?, SHA2(?, 256), ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, loginName);
                stmt.setString(2, name);
                stmt.setString(3, password);
                stmt.setString(4, String.valueOf(type));
                stmt.setObject(5, (type == 'B') ? shopId : null);
                stmt.setObject(6, (type == 'W') ? warehouseId : null);
                stmt.executeUpdate();
            }

            conn.commit();
            response.sendRedirect("userListServlet?message=user_added");

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
                String errorMsg = URLEncoder.encode(e.getMessage(), "UTF-8");
                response.sendRedirect("addUserServlet?error=" + errorMsg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<Long, String> getReferenceData(Connection conn, String table) throws SQLException {
        Map<Long, String> data = new LinkedHashMap<>();
        String sql = "SELECT id, name FROM " + table;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.put(rs.getLong("id"), rs.getString("name"));
            }
        }
        return data;
    }

    private String generateNewLoginName(Connection conn, char type) throws SQLException {
        String prefix = "";
        switch (type) {
            case 'B': prefix = "shop_"; break;
            case 'W': prefix = "wh_"; break;
            case 'S': prefix = "mgr_"; break;
            case 'M': prefix = "admin_"; break;
            default: prefix = "user_";
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT MAX(loginName) FROM user WHERE loginName LIKE ?")) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            
            int nextNum = 1;
            if (rs.next()) {
                String maxName = rs.getString(1);
                if (maxName != null) {
                    nextNum = Integer.parseInt(maxName.substring(prefix.length())) + 1;
                }
            }
            return prefix + nextNum;
        }
    }

    private Long parseLong(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}