/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package AIB.Bean;

import java.beans.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import AIB.algorithm.SnowflakeSingleton;
import AIB.db.ITP4511_DB;

/**
 *
 * @author andyt
 */
public class BorrowBean implements Serializable {
    private final ITP4511_DB db;

    public BorrowBean(ITP4511_DB db) {
        this.db = db;
    }

    public Map<String, Map<String, Object>> getCityShopsStock(long currentShopId, long cityId) throws SQLException {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        String sql = "SELECT s.id AS shopId, s.name AS shopName, f.id AS fruitId, f.name AS fruitName, ss.num "
                   + "FROM shop s "
                   + "JOIN shopStock ss ON s.id = ss.shopid "
                   + "JOIN fruit f ON ss.fruitid = f.id "
                   + "WHERE s.cityid = ? AND s.id != ?";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, cityId);
            stmt.setLong(2, currentShopId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String key = rs.getLong("shopId") + "|" + rs.getLong("fruitId");
                Map<String, Object> item = new HashMap<>();
                item.put("shopName", rs.getString("shopName"));
                item.put("fruitName", rs.getString("fruitName"));
                item.put("quantity", rs.getInt("num"));
                result.put(key, item);
            }
        }
        return result;
    }

    public boolean createBorrowRequest(long sourceShopId, long destShopId, Map<Long, Integer> items) throws SQLException {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            long borrowId = SnowflakeSingleton.getInstance().nextId();
            
            // Create borrow record
            String borrowSql = "INSERT INTO borrow (id, sourceShopid, destinationShopid, state) VALUES (?, ?, ?, 'C')";
            try (PreparedStatement stmt = conn.prepareStatement(borrowSql)) {
                stmt.setLong(1, borrowId);
                stmt.setLong(2, sourceShopId);
                stmt.setLong(3, destShopId);
                stmt.executeUpdate();
            }

            // Add borrow details
            String detailSql = "INSERT INTO borrowDetail (borrowid, fruitid, num) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(detailSql)) {
                for (Map.Entry<Long, Integer> entry : items.entrySet()) {
                    stmt.setLong(1, borrowId);
                    stmt.setLong(2, entry.getKey());
                    stmt.setInt(3, entry.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
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
    
}
