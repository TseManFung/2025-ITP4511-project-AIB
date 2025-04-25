package AIB.servlet;

import AIB.Bean.SkuReportBean;
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

@WebServlet("/skuReportServlet")
public class SkuReportServlet extends HttpServlet {

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
        List<SkuReportBean> skuReports = new ArrayList<>();
        List<String> countries = new ArrayList<>();

        // Get filter and sort parameters
        String countryId = request.getParameter("countryId");
        String sortBy = request.getParameter("sortBy") != null ? request.getParameter("sortBy") : "countryName";

        try (Connection conn = db.getConnection()) {
            // Verify warehouse type
            PreparedStatement stmt = conn.prepareStatement("SELECT type, countryid FROM warehouse WHERE id = ?");
            stmt.setLong(1, warehouseId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || !"S".equals(rs.getString("type"))) {
                response.sendRedirect("login.jsp?error=access_denied");
                return;
            }
            Long sourceCountryId = rs.getLong("countryid");

            // Get all countries for filter dropdown (including source country)
            stmt = conn.prepareStatement("SELECT id, name FROM country");
            rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                // Append "(Your Country)" to the source country's name
                if (id.equals(sourceCountryId)) {
                    name += " (Your Country)";
                }
                countries.add(id + ":" + name);
            }

            // Build SQL query
            StringBuilder sql = new StringBuilder(
                "WITH AvgDeliveryDays AS (" +
                "    SELECT " +
                "        wsc.destinationWarehouseid, " +
                "        ROUND(AVG(DATEDIFF(wsc.deliveryEndTime, wsc.deliveryStartTime))) AS avg_days " +
                "    FROM warehouseStockChange wsc " +
                "    WHERE wsc.sourceWarehouseid = ? " +
                "        AND wsc.state = 'F' " +
                "    GROUP BY wsc.destinationWarehouseid " +
                "), " +
                "TargetCountries AS (" +
                "    SELECT " +
                "        wh.id AS central_warehouse_id, " +
                "        c.id AS country_id, " +
                "        c.name AS country_name, " +
                "        ad.avg_days " +
                "    FROM warehouse wh " +
                "    JOIN country c ON wh.countryid = c.id " +
                "    JOIN AvgDeliveryDays ad ON wh.id = ad.destinationWarehouseid " +
                "    WHERE wh.type = 'C' " +
                "), " +
                "Reservations AS (" +
                "    SELECT " +
                "        tc.country_id, " +
                "        rd.fruitid, " +
                "        SUM(rd.num) AS total_needed " +
                "    FROM reserve r " +
                "    JOIN reserveDetail rd ON r.id = rd.reserveid " +
                "    JOIN shop s ON r.Shopid = s.id " +
                "    JOIN city ci ON s.cityid = ci.id " +
                "    JOIN TargetCountries tc ON ci.countryid = tc.country_id " +
                "    WHERE r.state = 'A' " +
                "        AND DATE(r.reserveDT) = CURDATE() + INTERVAL tc.avg_days DAY " +
                "    GROUP BY tc.country_id, rd.fruitid " +
                "), " +
                "SourceStock AS (" +
                "    SELECT fruitid " +
                "    FROM warehouseStock " +
                "    WHERE warehouseid = ? " +
                "        AND num > 0 " +
                ") " +
                "SELECT " +
                "    tc.country_id, " +
                "    tc.country_name, " +
                "    f.name AS fruit_name, " +
                "    tc.avg_days, " +
                "    r.total_needed, " +
                "    CASE WHEN ss.fruitid IS NOT NULL THEN 1 ELSE 0 END AS stock_available " +
                "FROM TargetCountries tc " +
                "CROSS JOIN fruit f " +
                "JOIN SourceStock ss ON f.id = ss.fruitid " +
                "LEFT JOIN Reservations r ON tc.country_id = r.country_id AND f.id = r.fruitid "
            );

            List<Object> params = new ArrayList<>();
            params.add(warehouseId); // For AvgDeliveryDays
            params.add(warehouseId); // For SourceStock

            if (countryId != null && !countryId.isEmpty()) {
                sql.append("WHERE tc.country_id = ? ");
                params.add(Long.parseLong(countryId));
            }

            sql.append("ORDER BY ");
            switch (sortBy) {
                case "fruitName":
                    sql.append("fruit_name");
                    break;
                case "avgDeliveryDays":
                    sql.append("avg_days");
                    break;
                case "totalNeeded":
                    sql.append("total_needed DESC");
                    break;
                default:
                    sql.append("country_name");
                    break;
            }

            // Execute query
            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                SkuReportBean report = new SkuReportBean();
                report.setCountryId(rs.getLong("country_id"));
                report.setCountryName(rs.getString("country_name"));
                report.setFruitName(rs.getString("fruit_name"));
                report.setAvgDeliveryDays(rs.getLong("avg_days"));
                report.setTotalNeeded(rs.getLong("total_needed"));
                report.setStockAvailable(rs.getInt("stock_available") == 1);
                skuReports.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
        }

        request.setAttribute("skuReports", skuReports);
        request.setAttribute("countries", countries);
        request.getRequestDispatcher("/Warehouse/skuReport.jsp").forward(request, response);
    }
}