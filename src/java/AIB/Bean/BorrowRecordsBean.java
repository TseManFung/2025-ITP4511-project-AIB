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
    private final BorrowBean borrowBean;

    public BorrowRecordsBean(ITP4511_DB db, BorrowBean borrowBean) {
        this.db = db;
        this.borrowBean = borrowBean;
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

    public boolean updateRecordState(long recordId, String newState, long currentShopId) throws SQLException {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            // Get borrow details
            String checkSql = "SELECT sourceShopId, destinationShopId, state FROM borrow WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setLong(1, recordId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) return false;

            long sourceShopId = rs.getLong("sourceShopId");
            long destShopId = rs.getLong("destinationShopId");

            // Validate permission
            if ("A".equals(newState) || "R".equals(newState)) {
                if (sourceShopId != currentShopId) return false;
            } else if ("F".equals(newState)) {
                if (destShopId != currentShopId) return false;
            }

            // Update state
            String updateSql = "UPDATE borrow SET state = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, newState);
                updateStmt.setLong(2, recordId);
                updateStmt.executeUpdate();
            }

            // Adjust stock based on state
            List<Map<String, Object>> items = getBorrowDetails(recordId, conn);
            if ("A".equals(newState)) {
                for (Map<String, Object> item : items) {
                    long fruitId = (Long) item.get("fruitid");
                    int num = (Integer) item.get("num");
                    borrowBean.updateFruitStock(sourceShopId, fruitId, -num);
                    borrowBean.updateFruitStock(destShopId, fruitId, num);
                }
            } else if ("F".equals(newState)) {
                // Additional logic if needed
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    private List<Map<String, Object>> getBorrowDetails(long recordId, Connection conn) throws SQLException {
        List<Map<String, Object>> items = new ArrayList<>();
        String sql = "SELECT fruitid, num FROM borrowDetail WHERE borrowid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, recordId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("fruitid", rs.getLong("fruitid"));
                item.put("num", rs.getInt("num"));
                items.add(item);
            }
        }
        return items;
    }
}
