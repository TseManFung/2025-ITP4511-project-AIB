package AIB.servlet;

import AIB.Bean.StockBean;
import AIB.db.ITP4511_DB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/AddStockServlet")
public class AddStockServlet extends HttpServlet {

    private ITP4511_DB db;

    @Override
    public void init() {
        String dbUser = this.getServletContext().getInitParameter("dbUser");
        String dbPassword = this.getServletContext().getInitParameter("dbPassword");
        String dbUrl = this.getServletContext().getInitParameter("dbUrl");
        db = new ITP4511_DB(dbUrl, dbUser, dbPassword);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !("S".equals(session.getAttribute("warehouseType")) || "C".equals(session.getAttribute("warehouseType")))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String warehouseId = session.getAttribute("warehouseId").toString();
        String warehouseType = session.getAttribute("warehouseType").toString();
        List<StockBean> stockList = new ArrayList<>();

        try (Connection conn = db.getConnection()) {
            String sql;
            PreparedStatement stmt;

            if ("C".equals(warehouseType)) {
                sql = "SELECT f.id, f.name, ws.num "
                        + "FROM fruit f "
                        + "JOIN warehouseStock ws ON f.id = ws.fruitid "
                        + "WHERE ws.warehouseid = ? AND ws.num > 0";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, warehouseId);
            } else {
                sql = "SELECT f.id, f.name, ws.num "
                        + "FROM fruit f "
                        + "JOIN warehouseStock ws ON f.id = ws.fruitid "
                        + "JOIN warehouse w ON ws.warehouseid = w.id "
                        + "WHERE ws.warehouseid = ? AND ws.num != 0";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, warehouseId);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stockList.add(new StockBean(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getInt("num"),
                        warehouseId
                ));
            }

            // 調試輸出
            System.out.println("Warehouse ID: " + warehouseId + ", Type: " + warehouseType);
            System.out.println("StockList size: " + stockList.size());
            for (StockBean stock : stockList) {
                System.out.println("Fruit ID: " + stock.getFruitId() + ", Name: " + stock.getFruitName() + ", Num: " + stock.getCurrentStock());
            }

            request.setAttribute("stockList", stockList);
            request.getRequestDispatcher("/Warehouse/addStock.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !("S".equals(session.getAttribute("warehouseType")) || "C".equals(session.getAttribute("warehouseType")))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String warehouseId = session.getAttribute("warehouseId").toString();

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            // Process each fruit quantity
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if (entry.getKey().startsWith("fruit_")) {
                    String fruitId = entry.getKey().substring(6);
                    int addQuantity;

                    try {
                        addQuantity = Integer.parseInt(entry.getValue()[0]);
                    } catch (NumberFormatException e) {
                        conn.rollback();
                        response.sendRedirect("AddStockServlet?error=invalid");
                        return;
                    }

                    if (addQuantity < 0) {
                        conn.rollback();
                        response.sendRedirect("AddStockServlet?error=invalid");
                        return;
                    }

                    // Check if stock record exists
                    String checkSql = "SELECT num FROM warehouseStock WHERE warehouseid = ? AND fruitid = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setString(1, warehouseId);
                    checkStmt.setString(2, fruitId);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        // Update existing stock
                        String updateSql = "UPDATE warehouseStock SET num = num + ? "
                                + "WHERE warehouseid = ? AND fruitid = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setInt(1, addQuantity);
                        updateStmt.setString(2, warehouseId);
                        updateStmt.setString(3, fruitId);
                        updateStmt.executeUpdate();
                    } else {
                        // Insert new stock record
                        String insertSql = "INSERT INTO warehouseStock (warehouseid, fruitid, num) VALUES (?, ?, ?)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                        insertStmt.setString(1, warehouseId);
                        insertStmt.setString(2, fruitId);
                        insertStmt.setInt(3, addQuantity);
                        insertStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            response.sendRedirect("AddStockServlet?success=true");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("AddStockServlet?error=database");
        }
    }
}
