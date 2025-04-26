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

import AIB.Bean.FruitBean;
import AIB.Bean.ShopBean;
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

    public ShopBean getShopStock(long shopId) throws SQLException {
        ShopBean shop = new ShopBean();
        shop.setShopid(shopId);

        String sql = "SELECT f.id AS fruitid, f.name, f.unit, ss.num " +
                "FROM shopStock ss " +
                "JOIN fruit f ON ss.fruitid = f.id " +
                "WHERE ss.shopid = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, shopId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FruitBean fruit = new FruitBean();
                fruit.setId(rs.getLong("fruitid"));
                fruit.setName(rs.getString("name"));
                fruit.setUnit(rs.getString("unit"));
                fruit.setOriginalNum(rs.getInt("num")); // 原始數量
                shop.getFruits().add(fruit);
            }
        }
        return shop;
    }

    public ShopBean getShopStock(long shopId, List<Long> fruitIds) throws SQLException {
        ShopBean shop = new ShopBean();
        shop.setShopid(shopId);

        String sql = "SELECT f.id AS fruitid, f.name, ss.num " +
                "FROM shopStock ss " +
                "JOIN fruit f ON ss.fruitid = f.id " +
                "WHERE ss.shopid = ? AND ss.fruitid IN (" +
                String.join(",", fruitIds.stream().map(id -> "?").toArray(String[]::new)) + ")";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, shopId);
            for (int i = 0; i < fruitIds.size(); i++) {
                stmt.setLong(i + 2, fruitIds.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FruitBean fruit = new FruitBean();
                fruit.setId(rs.getLong("fruitid"));
                fruit.setName(rs.getString("name"));
                fruit.setOriginalNum(rs.getInt("num")); // 原始數量
                shop.getFruits().add(fruit);
            }
        }
        return shop;
    }

    public ShopBean updateStock(long shopId, Map<Long, Integer> updates) throws SQLException {
        Connection conn = null;
        ShopBean shop = getShopStock(shopId); // 獲取當前商店的庫存
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            String updateSql = "UPDATE shopStock SET num = ? WHERE shopid = ? AND fruitid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                for (FruitBean fruit : shop.getFruits()) {
                    if (updates.containsKey(fruit.getId())) {
                        int consumeNum = updates.get(fruit.getId());
                        int newValue = fruit.getOriginalNum() - consumeNum;
                        if (newValue < 0) {
                            throw new SQLException("Negative stock not allowed for fruit ID: " + fruit.getId());
                        }
                        fruit.setConsumeNum(consumeNum);
                        fruit.setUpdatedNum(newValue);

                        stmt.setInt(1, newValue);
                        stmt.setLong(2, shopId);
                        stmt.setLong(3, fruit.getId());
                        stmt.addBatch();
                    }
                }
                stmt.executeBatch();
            }

            // 創建消耗記錄
            long consumeId = SnowflakeSingleton.getInstance().nextId();
            String consumeSql = "INSERT INTO consume (id, shopid) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(consumeSql)) {
                stmt.setLong(1, consumeId);
                stmt.setLong(2, shopId);
                stmt.executeUpdate();
            }

            // 插入消耗詳細記錄
            String detailSql = "INSERT INTO consumeDetail (consumeid, fruitid, num) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(detailSql)) {
                for (FruitBean fruit : shop.getFruits()) {
                    if (fruit.getConsumeNum() > 0) {
                        stmt.setLong(1, consumeId);
                        stmt.setLong(2, fruit.getId());
                        stmt.setInt(3, fruit.getConsumeNum());
                        stmt.addBatch();
                    }
                }
                stmt.executeBatch();
            }

            conn.commit();
            return shop;
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
