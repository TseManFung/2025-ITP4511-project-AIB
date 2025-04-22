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

@WebServlet("/editUserServlet")
public class EditUserServlet extends HttpServlet {

    private ITP4511_DB db;

    // Initialize database connection using context parameters
    @Override
    public void init() {
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        db = new ITP4511_DB(dbUrl, dbUser, dbPassword);
    }

    // Handle GET request to display edit form
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"S".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        String loginName = request.getParameter("loginName");
        UserBean user = null;
        
        try (Connection conn = db.getConnection()) {
            // Get reference data
            Map<Long, String> shops = getReferenceData(conn, "shop");
            Map<Long, String> warehouses = getReferenceData(conn, "warehouse");
            
            // Get user details
            user = getUserDetails(conn, loginName);
            
            if (user == null) {
                response.sendRedirect("userListServlet?error=user_not_found");
                return;
            }
            
            request.setAttribute("shops", shops);
            request.setAttribute("warehouses", warehouses);
            request.setAttribute("user", user);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }
        
        request.getRequestDispatcher("/Manager/editUser.jsp").forward(request, response);
    }

    // Handle POST request for form submission
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"S".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        String originalLoginName = request.getParameter("originalLoginName");
        String newLoginName = request.getParameter("loginName");
        String newName = request.getParameter("name");
        String newPassword = request.getParameter("password");
        char newType = request.getParameter("type").charAt(0);
        Long shopId = parseLong(request.getParameter("shopId"));
        Long warehouseId = parseLong(request.getParameter("warehouseId"));

        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            char originalType = getOriginalUserType(conn, originalLoginName);
            
            if (originalType != newType) {
                handleRoleChange(
                    conn, 
                    originalLoginName,
                    newLoginName,
                    newName,
                    newPassword,
                    newType,
                    shopId,
                    warehouseId
                );
            } else {
                updateExistingUser(
                    conn,
                    originalLoginName,
                    newLoginName,
                    newName,
                    newPassword,
                    shopId,
                    warehouseId
                );
            }
            
            conn.commit();
            response.sendRedirect("userListServlet?message=update_success");
            
        } catch (SQLException e) {
            handleSQLException(conn, response, e);
        } finally {
            cleanupResources(conn);
        }
    }

    // Retrieve reference data from database
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

    // Get user details from database
    private UserBean getUserDetails(Connection conn, String loginName) throws SQLException {
        UserBean user = null;
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM user WHERE loginName = ?")) {
            
            stmt.setString(1, loginName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                user = new UserBean();
                user.setLoginName(rs.getString("loginName"));
                user.setName(rs.getString("name"));
                user.setType(rs.getString("type").charAt(0));
                user.setShopId(rs.getLong("shopid"));
                user.setWarehouseId(rs.getLong("warehouseid"));
            }
        }
        return user;
    }

    // Handle role change logic
    private void handleRoleChange(Connection conn, 
                                String originalLoginName,
                                String newLoginName,
                                String newName,
                                String newPassword,
                                char newType,
                                Long shopId,
                                Long warehouseId) throws SQLException {
        
        validateRequiredIds(newType, shopId, warehouseId);
        
        if (newLoginName == null || newLoginName.trim().isEmpty()) {
            newLoginName = generateNewLoginName(conn, newType);
        }

        createNewUser(
            conn,
            newLoginName,
            newName,
            newPassword,
            newType,
            shopId,
            warehouseId
        );

        deleteOriginalUser(conn, originalLoginName);
    }

    // Validate required IDs based on role
    private void validateRequiredIds(char type, Long shopId, Long warehouseId) throws SQLException {
        if (type == 'B' && shopId == null) {
            throw new SQLException("Shop selection required");
        }
        if (type == 'W' && warehouseId == null) {
            throw new SQLException("Warehouse selection required");
        }
    }

    // Create new user record
    private void createNewUser(Connection conn,
                             String loginName,
                             String name,
                             String password,
                             char type,
                             Long warehouseId,
                             Long shopId) throws SQLException {
        String sql = "INSERT INTO user (loginName, name, password, type, warehouseid, shopid) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, loginName);
            stmt.setString(2, name);
            stmt.setString(3, password);
            stmt.setString(4, String.valueOf(type));
            stmt.setObject(5, warehouseId);
            stmt.setObject(6, shopId);
            stmt.executeUpdate();
        }
    }

    // Delete original user
    private void deleteOriginalUser(Connection conn, String loginName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM user WHERE loginName = ?")) {
            stmt.setString(1, loginName);
            stmt.executeUpdate();
        }
    }

    // Update existing user
    private void updateExistingUser(Connection conn,
                                  String originalLoginName,
                                  String newLoginName,
                                  String newName,
                                  String newPassword,
                                  Long shopId,
                                  Long warehouseId) throws SQLException {
        String sql = "UPDATE user SET "
                   + "loginName = ?, "
                   + "name = ?, "
                   + "password = COALESCE(?, password), "
                   + "shopid = ?, "
                   + "warehouseid = ? "
                   + "WHERE loginName = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newLoginName);
            stmt.setString(2, newName);
            stmt.setString(3, newPassword);
            stmt.setObject(4, shopId);
            stmt.setObject(5, warehouseId);
            stmt.setString(6, originalLoginName);
            stmt.executeUpdate();
        }
    }

    // Helper methods
    private Long parseLong(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private char getOriginalUserType(Connection conn, String loginName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT type FROM user WHERE loginName = ?")) {
            stmt.setString(1, loginName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("type").charAt(0);
            }
            throw new SQLException("User not found: " + loginName);
        }
    }

    private String generateNewLoginName(Connection conn, char newType) throws SQLException {
        String prefix = "";
        switch (newType) {
            case 'B':
                prefix = "shop_";
                break;
            case 'W':
                prefix = "wh_";
                break;
            case 'S':
                prefix = "mgr_";
                break;
            case 'M':
                prefix = "admin_";
                break;
            default:
                prefix = "user_";
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

    private void handleSQLException(Connection conn, HttpServletResponse response, SQLException e) {
        try {
            if (conn != null) conn.rollback();
            String errorMsg = "database_error";
            if (e.getMessage().contains("Shop selection")) errorMsg = "shop_required";
            if (e.getMessage().contains("Warehouse selection")) errorMsg = "warehouse_required";
            response.sendRedirect("userListServlet?error=" + errorMsg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cleanupResources(Connection conn) {
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