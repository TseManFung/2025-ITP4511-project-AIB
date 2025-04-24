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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
public class Borrow implements Serializable {

    private final ITP4511_DB db;

    public Borrow(ITP4511_DB db) {
        this.db = db;
    }

    public List<ShopBean> getCityShopsStock(long currentShopId, long cityId) throws SQLException {
        List<ShopBean> shopList = new ArrayList<>();
String sql = "SELECT " +
        "shop.id AS shopId, " +
        "shop.name AS shopName, " +
        "city.id AS cityId, " +
        "city.name AS cityName, " +
        "country.id AS countryId, " +
        "country.name AS countryName, " +
        "shopStock.fruitid AS fruitId, " +
        "fruit.name AS fruitName, " +
        "fruit.unit AS fruitUnit, " +
        "(shopStock.num - COALESCE(borrowed.num, 0)) AS availableQuantity " +
        "FROM " +
        "shop " +
        "INNER JOIN city ON shop.cityid = city.id " +
        "INNER JOIN country ON city.countryid = country.id " +
        "INNER JOIN shopStock ON shop.id = shopStock.shopid " +
        "INNER JOIN fruit ON shopStock.fruitid = fruit.id " +
        "LEFT JOIN ( " +
        "SELECT " +
        "bd.fruitid, " +
        "b.sourceShopid, " +
        "SUM(bd.num) AS num " +
        "FROM " +
        "borrow b " +
        "INNER JOIN borrowDetail bd ON b.id = bd.borrowid " +
        "WHERE " +
        "b.state = 'C' " +
        "GROUP BY " +
        "bd.fruitid, b.sourceShopid " +
        ") AS borrowed ON shop.id = borrowed.sourceShopid AND shopStock.fruitid = borrowed.fruitid " +
        "WHERE " +
        "shop.cityid = ? AND shop.id != ? " +
        "ORDER BY " +
        "shop.id;";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, cityId);
            stmt.setLong(2, currentShopId);
            ResultSet rs = stmt.executeQuery();

            Long currentShopIdInLoop = null;
            ShopBean currentShop = null;

            while (rs.next()) {
                Long shopId = rs.getLong("shopId");

                if (!shopId.equals(currentShopIdInLoop)) {
                    if (currentShop != null) {
                        shopList.add(currentShop);
                    }
                    currentShopIdInLoop = shopId;
                    currentShop = new ShopBean(shopId, rs.getString("shopName"),
                            rs.getLong("cityId"), rs.getString("cityName"),
                            rs.getLong("countryId"), rs.getString("countryName"));
                }

                if (currentShop != null) {
                    FruitBean fruit = new FruitBean();
                    fruit.setId(rs.getLong("fruitId"));
                    fruit.setName(rs.getString("fruitName"));
                    fruit.setUnit(rs.getString("fruitUnit"));
                    fruit.setQuantity(rs.getInt("stockQuantity"));
                    currentShop.getFruits().add(fruit);
                }
            }

            if (currentShop != null) {
                shopList.add(currentShop);
            }
        }

        return shopList;
    }

    public long createBorrowRequest(long sourceShopId, long destShopId, Map<Long, Integer> items) throws SQLException {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            long borrowId = SnowflakeSingleton.getInstance().nextId();

            String borrowSql = "INSERT INTO borrow (id, sourceShopid, destinationShopid, state) VALUES (?, ?, ?, 'C')";
            try (PreparedStatement stmt = conn.prepareStatement(borrowSql)) {
                stmt.setLong(1, borrowId);
                stmt.setLong(3, sourceShopId);
                stmt.setLong(2, destShopId);
                stmt.executeUpdate();
            }

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
            return borrowId;
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
