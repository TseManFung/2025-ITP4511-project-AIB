package AIB.servlet;

import AIB.db.ITP4511_DB;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/deleteUserServlet")
public class DeleteUserServlet extends HttpServlet {
    private ITP4511_DB db;

    @Override
    public void init() {
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        db = new ITP4511_DB(dbUrl, dbUser, dbPassword);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"S".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        String loginName = request.getParameter("loginName");
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE user SET type = 'D' WHERE loginName = ?")) {
            
            stmt.setString(1, loginName);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                response.sendRedirect("userListServlet?message=delete_success");
            } else {
                response.sendRedirect("userListServlet?error=user_not_found");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("userListServlet?error=database_error");
        }
    }
}