/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AIB.servlet;

import AIB.db.ITP4511_DB;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "loginServlet", urlPatterns = {"/loginServlet"})
public class loginServlet extends HttpServlet {

    private ITP4511_DB db;

    @Override
    public void init() {
        String dbUser = this.getServletContext().getInitParameter("dbUser");
        String dbPassword = this.getServletContext().getInitParameter("dbPassword");
        String dbUrl = this.getServletContext().getInitParameter("dbUrl");
        db = new ITP4511_DB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("login".equals(action)) {
            handleLogin(request, response);
        } else if ("logout".equals(action)) {
            handleLogout(request, response);
        } else {
            response.sendRedirect("login.jsp?error=invalid_action");
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");

        if (loginName == null || loginName.isEmpty() || password == null || password.isEmpty()) {
            response.sendRedirect("login.jsp?error=empty_fields");
            return;
        }

        try (Connection conn = db.getConnection()) {
            String sql = "SELECT loginName, name, type, warehouseid, shopid FROM user WHERE loginName = ? AND password = ? AND type != 'D'";
            PreparedStatement stmt = conn.prepareStatement(sql);

            String hashedPassword = hashPassword(password);

            stmt.setString(1, loginName);
            stmt.setString(2, hashedPassword);

            System.out.println("Parameters - loginName: " + loginName + ", password: [hashed]");

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession();
                String userType = rs.getString("type");
                Long countryId = null;
                Long cityId = null;

                session.setAttribute("loginName", rs.getString("loginName"));
                session.setAttribute("userType", userType);
                session.setAttribute("userName", rs.getString("name"));

                if (userType.equals("B")) {
                    Long shopId = rs.getLong("shopid");
                    if (!rs.wasNull()) {
                        session.setAttribute("shopId", shopId);

                        String shopQuery = "SELECT s.cityid, c.countryid FROM shop s "
                                + "JOIN city c ON s.cityid = c.id WHERE s.id = ?";
                        try (PreparedStatement shopStmt = conn.prepareStatement(shopQuery)) {
                            shopStmt.setLong(1, shopId);
                            ResultSet shopRs = shopStmt.executeQuery();
                            if (shopRs.next()) {
                                cityId = shopRs.getLong("cityid");
                                countryId = shopRs.getLong("countryid");
                            } else {
                                response.sendRedirect("login.jsp?error=invalid_shop");
                                return;
                            }
                        }
                    } else {
                        response.sendRedirect("login.jsp?error=missing_shop_id");
                        return;
                    }
                } else if (userType.equals("W")) {
                    Long warehouseId = rs.getLong("warehouseid");
                    if (!rs.wasNull()) {
                        session.setAttribute("warehouseId", warehouseId);

                        String warehouseQuery = "SELECT countryid, type FROM warehouse WHERE id = ?";
                        try (PreparedStatement warehouseStmt = conn.prepareStatement(warehouseQuery)) {
                            warehouseStmt.setLong(1, warehouseId);
                            ResultSet warehouseRs = warehouseStmt.executeQuery();
                            if (warehouseRs.next()) {
                                countryId = warehouseRs.getLong("countryid");
                                String warehouseType = warehouseRs.getString("type");
                                if (!"S".equals(warehouseType) && !"C".equals(warehouseType)) {
                                    response.sendRedirect("login.jsp?error=invalid_warehouse_type");
                                    return;
                                }
                                session.setAttribute("warehouseType", warehouseType);
                            } else {
                                response.sendRedirect("login.jsp?error=invalid_warehouse");
                                return;
                            }
                        }
                    } else {
                        response.sendRedirect("login.jsp?error=missing_warehouse_id");
                        return;
                    }
                    cityId = null;
                } else if (userType.equals("S") || userType.equals("M")) {
                    // Senior management or Manager doesn't need shopId or warehouseId
                } else {
                    response.sendRedirect("login.jsp?error=invalid_role");
                    return;
                }

                session.setAttribute("countryId", countryId);
                session.setAttribute("cityId", cityId);

                redirectBasedOnRole(userType, response);
            } else {
                response.sendRedirect("login.jsp?error=invalid_credentials");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=database_error");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=server_error");
        }
    }

    private void redirectBasedOnRole(String userType, HttpServletResponse response) throws IOException {
        switch (userType) {
            case "B": // Bakery shop staff
                response.sendRedirect("Shop/bakeryDashboard.jsp");
                break;
            case "W": // Warehouse staff
                response.sendRedirect("Warehouse/warehouseDashboard.jsp");
                break;
            case "S": // Senior management
            case "M": // Manager
                response.sendRedirect("Manager/managementDashboard.jsp");
                break;
            default:
                response.sendRedirect("login.jsp?error=invalid_role");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("Logging out user: " + session.getAttribute("loginName"));
            session.invalidate();
            System.out.println("Session invalidated successfully");
        } else {
            System.out.println("No active session found during logout");
        }
        response.sendRedirect("login.jsp?message=logout_success");
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(password.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
