package AIB.servlet;

import AIB.Bean.UserBean;

import AIB.db.ITP4511_DB;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/userListServlet")
public class UserListServlet extends HttpServlet {

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

        List<UserBean> userList = new ArrayList<>();
        String error = null;

        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM user WHERE type != 'D'";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                UserBean user = new UserBean();
                user.setLoginName(rs.getString("loginName"));
                user.setName(rs.getString("name"));
                String typeStr = rs.getString("type");
                // Validate type
                if (typeStr != null && !typeStr.isEmpty()) {
                    user.setType(typeStr.charAt(0));
                } 
                user.setWarehouseId(rs.getLong("warehouseid"));
                user.setShopId(rs.getLong("shopid"));
                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            error = "Database error: " + e.getMessage();
        }

        request.setAttribute("userList", userList);
        if (error != null) {
            request.setAttribute("error", error);
        }
        request.getRequestDispatcher("/Manager/userList.jsp").forward(request, response);
    }
}