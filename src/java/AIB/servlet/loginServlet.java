/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AIB.servlet;


//import other file
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
    System.out.println("processRequest called - action: " + request.getParameter("action")); // Debug log
    
    String action = request.getParameter("action");
    
    if ("login".equals(action)) {
        System.out.println("Handling login action"); // Debug log
        handleLogin(request, response);
    } else if ("logout".equals(action)) {
        System.out.println("Handling logout action"); // Debug log
        handleLogout(request, response);
    } else {
        System.out.println("Invalid action: " + action); // Debug log
        response.sendRedirect("login.jsp?error=invalid_action");
    }
}

private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    System.out.println("handleLogin method started"); // Debug log
    
    String loginName = request.getParameter("loginName");
    String password = request.getParameter("password");
    
    System.out.println("Login attempt - username: " + loginName); // Debug log
    
    if (loginName == null || loginName.isEmpty() || password == null || password.isEmpty()) {
        System.out.println("Empty fields detected"); // Debug log
        response.sendRedirect("login.jsp?error=empty_fields");
        return;
    }
    
    try {
        System.out.println("Attempting database connection"); // Debug log
        Connection conn = db.getConnection();
        String sql = "SELECT * FROM user WHERE loginName = ? AND password = ? AND type != 'D'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        String hashedPassword = hashPassword(password);
        System.out.println("Password hashed"); // Debug log
        
        stmt.setString(1, loginName);
        stmt.setString(2, hashedPassword);
        
        System.out.println("Executing query: " + sql); // Debug log
        System.out.println("Parameters - loginName: " + loginName + ", password: [hashed]"); // Debug log
        
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            System.out.println("User found in database"); // Debug log
            HttpSession session = request.getSession();
       
            session.setAttribute("loginName", loginName);
            session.setAttribute("userType", rs.getString("type"));
            session.setAttribute("userName", rs.getString("name"));
            if(rs.getString("type").equals("B")){
                session.setAttribute("ID", rs.getString("shopid"));
            }else{
                session.setAttribute("ID", rs.getString("warehouseid"));
            }
            System.out.println("Session created for user: " + loginName); // Debug log
            System.out.println("User type: " + rs.getString("type")); // Debug log
            
            redirectBasedOnRole(rs.getString("type"), response);
        } else {
            System.out.println("No matching user found or invalid credentials"); // Debug log
            response.sendRedirect("login.jsp?error=invalid_credentials");
        }
        
        rs.close();
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage()); // Error log
        e.printStackTrace();
        response.sendRedirect("login.jsp?error=database_error");
    } catch (NoSuchAlgorithmException e) {
        System.err.println("Hashing algorithm error: " + e.getMessage()); // Error log
        e.printStackTrace();
        response.sendRedirect("login.jsp?error=server_error");
    }
}

    private void redirectBasedOnRole(String userType, HttpServletResponse response) throws IOException {
        switch (userType) {
            case "B": // Bakery shop staff
                response.sendRedirect("bakeryDashboard.jsp");
                break;
            case "W": // Warehouse staff
                response.sendRedirect("warehouseDashboard.jsp");
                break;
            case "S": // Senior management
                response.sendRedirect("Manager/managementDashboard.jsp");
                break;
            default:
                response.sendRedirect("login.jsp?error=invalid_role");
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Clear all session attributes
            session.removeAttribute("userId");
            session.removeAttribute("loginName");
            session.removeAttribute("userType");
            session.removeAttribute("userName");
            
            // Invalidate the session
            session.invalidate();
        }
        response.sendRedirect("login.jsp?message=logout_success");
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(password.getBytes());
        
        // Convert byte array to hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
}