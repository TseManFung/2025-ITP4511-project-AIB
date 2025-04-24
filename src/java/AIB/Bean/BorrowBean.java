package AIB.Bean;

import AIB.db.ITP4511_DB;
import AIB.algorithm.SnowflakeSingleton;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BorrowBean implements Serializable {

    private final ITP4511_DB db;

    public BorrowBean(ITP4511_DB db) {
        this.db = db;
    }

    // Get shops and their fruit stocks in the same city
    public List<ShopBean> getCityShopsStock(long currentShopId, long cityId) throws SQLException {
        List<ShopBean> shopList = new ArrayList<>();
        String sql = "SELECT shop.id AS shopId, shop.name AS shopName, city.id AS cityId, city.name AS cityName, " +
                     "country.id AS countryId, country.name AS countryName, shopStock.fruitid AS fruitId, " +
                     "fruit.name AS fruitName, fruit.unit AS fruitUnit, shopStock.num AS stockQuantity " +
                     "FROM shop " +
                     "INNER JOIN city ON shop.cityid = city.id " +
                     "INNER JOIN country ON city.countryid = country.id " +
                     "INNER JOIN shopStock ON shop.id = shopStock.shopid " +
                     "INNER JOIN fruit ON shopStock.fruitid = fruit.id " +
                     "WHERE shop.cityid = ? AND shop.id != ? " +
                     "ORDER BY shop.id";

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

    // Create a borrow request
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

    // Update fruit stock for both shops
    public void updateFruitStock(long shopId, long fruitId, int quantityChange) throws SQLException {
        String sql = "UPDATE shopStock SET num = num + ? WHERE shopid = ? AND fruitid = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantityChange);
            stmt.setLong(2, shopId);
            stmt.setLong(3, fruitId);
            stmt.executeUpdate();
        }
    }
}