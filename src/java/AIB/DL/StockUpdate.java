/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package AIB.DL;

import java.beans.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import AIB.algorithm.SnowflakeSingleton;
import AIB.db.ITP4511_DB;

/**
 *
 * @author andyt
 */
public class StockUpdate implements Serializable {

    private final ITP4511_DB db;

    public StockUpdate(ITP4511_DB db) {
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

    public Map<Long, Map<String, Object>> getShopStock(long shopId,List<Long> fruitId) throws SQLException {
        Map<Long, Map<String, Object>> stock = new HashMap<>();
        String sql = "SELECT f.id AS fruitid, f.name, ss.num " +
                     "FROM shopStock ss " +
                     "JOIN fruit f ON ss.fruitid = f.id " +
                     "WHERE ss.shopid = ? AND ss.fruitid IN (";
        for (int i = 0; i < fruitId.size(); i++) {
            sql += "?";
            if (i < fruitId.size() - 1) {
                sql += ",";
            }
        }
        sql += ")";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, shopId);
            for (int i = 0; i < fruitId.size(); i++) {
                stmt.setLong(i + 2, fruitId.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> fruitData = new HashMap<>();
                fruitData.put("name", rs.getString("name"));
                fruitData.put("originalNum", rs.getInt("num"));
                stock.put(rs.getLong("fruitid"), fruitData);
            }
        }
        return stock;
    }

    public Map<Long, Map<String, Object>> updateStock(long shopId, Map<Long, Integer> updates) throws SQLException {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            // Get original values
            Map<Long, Map<String, Object>> shopStock = getShopStock(shopId, updates.keySet().stream().toList());

            // Update shopStock
            String updateSql = "UPDATE shopStock SET num = ? WHERE shopid = ? AND fruitid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                for (Map.Entry<Long, Integer> entry : updates.entrySet()) {
                    Map<String, Object> fruitData = shopStock.get(entry.getKey());
                    if (fruitData == null) {
                        throw new SQLException("Fruit not found in shop stock");
                    }
                    int consumeNum = entry.getValue();
                    int newValue = (int) fruitData.get("originalNum") - consumeNum;
                    if (newValue < 0) {
                        throw new SQLException("Negative stock not allowed");
                    }
                    fruitData.put("updatedNum", newValue);
                    fruitData.put("consumeNum", consumeNum);

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
            return shopStock;
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
