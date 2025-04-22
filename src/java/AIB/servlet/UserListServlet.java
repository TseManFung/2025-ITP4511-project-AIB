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
        // 初始化数据库连接（复用现有配置）
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

        try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM user WHERE type != 'D'";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                UserBean user = new UserBean();
                user.setLoginName(rs.getString("loginName"));
                user.setName(rs.getString("name"));
                user.setType(rs.getString("type").charAt(0));
                user.setWarehouseId(rs.getLong("warehouseid"));
                user.setShopId(rs.getLong("shopid"));
                userList.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database select error");
        }

        // 将用户列表传递到JSP
        request.setAttribute("userList", userList);
        request.getRequestDispatcher("/Manager/userList.jsp").forward(request, response);
    }
}
