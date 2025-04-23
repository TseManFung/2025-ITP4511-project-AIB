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
import java.util.Map;

import AIB.algorithm.SnowflakeSingleton;
import AIB.db.ITP4511_DB;

/**
 *
 * @author andyt
 */
public class StockUpdateBean implements Serializable {

    private final ITP4511_DB db;

    public StockUpdateBean(ITP4511_DB db) {
        this.db = db;
    }

    public Map<Long, Map<String, Object>> getShopStock(long shopId) throws SQLException {
        Map<Long, Map<String, Object>> stock = new HashMap<>();
        String sql = "SELECT f.id AS fruitid, f.name, ss.num " +
                     "FROM shopStock ss " +
                     "JOIN fruit f ON ss.fruitid = f.id " +
                     "WHERE ss.shopid = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, shopId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> fruitData = new HashMap<>();
                fruitData.put("name", rs.getString("name"));
                fruitData.put("num", rs.getInt("num"));
                stock.put(rs.getLong("fruitid"), fruitData);
            }
        }
        return stock;
    }

    public Map<String, Object> updateStock(long shopId, Map<Long, Integer> updates) throws SQLException {
        Connection conn = null;
        Map<String, Object> result = new HashMap<>();
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            // Get original values
            Map<Long, Integer> original = getShopStock(shopId);
            result.put("original", original);

            // Update shopStock
            String updateSql = "UPDATE shopStock SET num = ? WHERE shopid = ? AND fruitid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                for (Map.Entry<Long, Integer> entry : updates.entrySet()) {
                    int newValue = original.get(entry.getKey()) - entry.getValue();
                    if (newValue < 0) {
                        throw new SQLException("Negative stock not allowed");
                    }

                    stmt.setInt(1, newValue);
                    stmt.setLong(2, shopId);
                    stmt.setLong(3, entry.getKey());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            // Create consume record
            long consumeId = SnowflakeSingleton.getInstance().nextId();
            String consumeSql = "INSERT INTO consume (id, shopid) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(consumeSql)) {
                stmt.setLong(1, consumeId);
                stmt.setLong(2, shopId);
                stmt.executeUpdate();
            }

            // Insert consume details
            String detailSql = "INSERT INTO consumeDetail (consumeid, fruitid, num) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(detailSql)) {
                for (Map.Entry<Long, Integer> entry : updates.entrySet()) {
                    stmt.setLong(1, consumeId);
                    stmt.setLong(2, entry.getKey());
                    stmt.setInt(3, entry.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
            result.put("updated", getShopStock(shopId));
            return result;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

}
