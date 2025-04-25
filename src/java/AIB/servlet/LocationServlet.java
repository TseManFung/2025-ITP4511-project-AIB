// File: AIB/servlet/LocationServlet.java
package AIB.servlet;

import AIB.db.ITP4511_DB;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

@WebServlet(name = "LocationServlet", urlPatterns = {"/locations"})
public class LocationServlet extends HttpServlet {
    private ITP4511_DB db;

    @Override
    public void init() throws ServletException {
        super.init();
        db = new ITP4511_DB(
            getServletContext().getInitParameter("dbUrl"),
            getServletContext().getInitParameter("dbUser"),
            getServletContext().getInitParameter("dbPassword")
        );
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String type = request.getParameter("type");
        List<Location> locations = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            String sql = null;
            switch(type) {
                case "shop":
                    sql = "SELECT id, name FROM shop";
                    break;
                case "city":
                    sql = "SELECT id, name FROM city";
                    break;
                case "country":
                    sql = "SELECT id, name FROM country";
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while(rs.next()) {
                    locations.add(new Location(
                        rs.getLong("id"),
                        rs.getString("name")
                    ));
                }
            }
        } catch(SQLException e) {
            throw new ServletException("Database error", e);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(locations));
    }

    private static class Location {
        private final Long id;
        private final String name;

        public Location(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}