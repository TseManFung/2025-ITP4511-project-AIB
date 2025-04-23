/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package AIB.Bean;

import AIB.db.ITP4511_DB;
import java.io.Serializable;
import java.sql.*;
import java.util.*;

/**
 *
 * @author andyt
 */
public class BorrowRecordsBean implements Serializable {

    private final ITP4511_DB db;

    public BorrowRecordsBean(ITP4511_DB db) {
        this.db = db;
    }

    public List<Map<String, Object>> getBorrowRecords(long shopId) throws SQLException {
        List<Map<String, Object>> records = new ArrayList<>();
        String sql = "SELECT b.id, b.DT, s1.name AS fromShop, s2.name AS toShop, b.state "
                + "FROM borrow b "
                + "JOIN shop s1 ON b.sourceShopId = s1.id "
                + "JOIN shop s2 ON b.destinationShopId = s2.id "
                + "WHERE (b.sourceShopId = ? OR b.destinationShopId = ?) "
                + "AND b.state != 'F' "
                + "ORDER BY b.DT DESC";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, shopId);
            stmt.setLong(2, shopId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", rs.getLong("id"));
                record.put("DT", rs.getTimestamp("DT"));
                record.put("fromShop", rs.getString("fromShop"));
                record.put("toShop", rs.getString("toShop"));
                record.put("state", rs.getString("state"));
                records.add(record);
            }
        }
        return records;
    }

    public Map<String, Object> getRecordDetails(long recordId) throws SQLException {
        Map<String, Object> details = new HashMap<>();
        String sql = "SELECT b.*, f.name AS fruitName, bd.num "
                + "FROM borrow b "
                + "JOIN borrowDetail bd ON b.id = bd.borrowid "
                + "JOIN fruit f ON bd.fruitid = f.id "
                + "WHERE b.id = ?";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, recordId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                details.put("id", rs.getLong("id"));
                details.put("sourceShopId", rs.getLong("sourceShopId"));
                details.put("destShopId", rs.getLong("destinationShopId"));
                details.put("state", rs.getString("state"));
                details.put("DT", rs.getTimestamp("DT"));

                List<Map<String, Object>> items = new ArrayList<>();
                do {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", rs.getString("fruitName"));
                    item.put("quantity", rs.getInt("num"));
                    items.add(item);
                } while (rs.next());
                details.put("items", items);
            }
        }
        return details;
    }

    public boolean updateRecordState(long recordId, String newState, long currentShopId) throws SQLException {
        String checkSql = "SELECT destinationShopId FROM borrow WHERE id = ?";
        String updateSql = "UPDATE borrow SET state = ? WHERE id = ?";

        try (Connection conn = db.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkSql); PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            // Validate permission
            checkStmt.setLong(1, recordId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getLong("destinationShopId") != currentShopId) {
                return false;
            }

            // Update state
            updateStmt.setString(1, newState);
            updateStmt.setLong(2, recordId);
            return updateStmt.executeUpdate() > 0;
        }
    }
}
