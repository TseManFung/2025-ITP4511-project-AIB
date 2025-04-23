package AIB.servlet;

import AIB.Bean.FruitBean;
import AIB.db.ITP4511_DB;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import AIB.algorithm.SnowflakeSingleton;

@WebServlet("/fruitServlet")
public class FruitServlet extends HttpServlet {
    private ITP4511_DB db;

    // Initialize database connection
    @Override
    public void init() {
        String dbUser = getServletContext().getInitParameter("dbUser");
        String dbPassword = getServletContext().getInitParameter("dbPassword");
        String dbUrl = getServletContext().getInitParameter("dbUrl");
        db = new ITP4511_DB(dbUrl, dbUser, dbPassword);
    }

    // Handle GET requests
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        // Check user type (only Senior Management 'S' can access)
        HttpSession session = request.getSession(false);
        if (session == null || !"S".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        switch (action) {
            case "list":
                listFruits(request, response);
                break;
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteFruit(request, response);
                break;
            default:
                listFruits(request, response);
        }
    }

    // Handle POST requests
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        // Check user type
        HttpSession session = request.getSession(false);
        if (session == null || !"S".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.jsp?error=access_denied");
            return;
        }

        if ("add".equals(action)) {
            addFruit(request, response);
        } else if ("edit".equals(action)) {
            updateFruit(request, response);
        } else {
            listFruits(request, response);
        }
    }

    // List all fruits
    private void listFruits(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<FruitBean> fruits = new ArrayList<>();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fruit")) {
            while (rs.next()) {
                FruitBean fruit = new FruitBean();
                fruit.setId(rs.getLong("id"));
                fruit.setSourceCountryId(rs.getLong("sourceCountryid"));
                fruit.setName(rs.getString("name"));
                fruit.setUnit(rs.getString("unit"));
                fruits.add(fruit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }
        request.setAttribute("fruits", fruits);
        request.getRequestDispatcher("/Manager/fruitList.jsp").forward(request, response);
    }

    // Show add form with country options
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<Long, String> countries = getCountries();
        request.setAttribute("countries", countries);
        request.getRequestDispatcher("/Manager/addFruit.jsp").forward(request, response);
    }

    // Show edit form with pre-filled data
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        FruitBean fruit = null;
        Map<Long, String> countries = getCountries();

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM fruit WHERE id = ?")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                fruit = new FruitBean();
                fruit.setId(rs.getLong("id"));
                fruit.setSourceCountryId(rs.getLong("sourceCountryid"));
                fruit.setName(rs.getString("name"));
                fruit.setUnit(rs.getString("unit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }
        request.setAttribute("fruit", fruit);
        request.setAttribute("countries", countries);
        request.getRequestDispatcher("/Manager/editFruit.jsp").forward(request, response);
    }

    // Add a new fruit
    private void addFruit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long sourceCountryId = Long.parseLong(request.getParameter("sourceCountryId"));
        String name = request.getParameter("name");
        String unit = request.getParameter("unit");
  
        Long id = SnowflakeSingleton.getInstance().nextId();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO fruit (id,sourceCountryid, name, unit) VALUES (?,?, ?, ?)")) {
            stmt.setLong(1, id);
            stmt.setLong(2, sourceCountryId);
            stmt.setString(3, name);
            stmt.setString(4, unit);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
            request.getRequestDispatcher("/Manager/addFruit.jsp").forward(request, response);
            return;
        }
        response.sendRedirect("fruitServlet");
    }

    // Update an existing fruit
    private void updateFruit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Long sourceCountryId = Long.parseLong(request.getParameter("sourceCountryId"));
        String name = request.getParameter("name");
        String unit = request.getParameter("unit");

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE fruit SET sourceCountryid = ?, name = ?, unit = ? WHERE id = ?")) {
            stmt.setLong(1, sourceCountryId);
            stmt.setString(2, name);
            stmt.setString(3, unit);
            stmt.setLong(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
            request.getRequestDispatcher("/Manager/editFruit.jsp").forward(request, response);
            return;
        }
        response.sendRedirect("fruitServlet");
    }

    // Delete a fruit
    private void deleteFruit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM fruit WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error");
        }
        response.sendRedirect("fruitServlet");
    }

    // Helper method to fetch countries
    private Map<Long, String> getCountries() {
        Map<Long, String> countries = new LinkedHashMap<>();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM country")) {
            while (rs.next()) {
                countries.put(rs.getLong("id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countries;
    }
}