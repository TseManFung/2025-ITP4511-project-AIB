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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import AIB.Bean.FruitBean;
import AIB.Bean.ShopBean;
import AIB.Bean.BorrowBean;
import AIB.db.ITP4511_DB;

/**
 *
 * @author andyt
 */
public class BorrowRecord implements Serializable {

    private ITP4511_DB db;

    public BorrowRecord(ITP4511_DB db) {
        this.db = db;
    }

    public List<BorrowBean> getBorrowRecords(long shopId, String filterState, String sortBy) throws SQLException {
        List<BorrowBean> BorrowRecords = new ArrayList<>();
        String sql = "SELECT b.id AS borrowId, b.DT AS borrowDate,s1.id as fromId, s1.name AS fromShop,s2.id as toId, s2.name AS toShop, "
                + "b.state AS borrowState, COUNT(bd.fruitid) AS itemCount "
                + "FROM borrow b "
                + "JOIN shop s1 ON b.sourceShopId = s1.id "
                + "JOIN shop s2 ON b.destinationShopId = s2.id "
                + "LEFT JOIN borrowDetail bd ON b.id = bd.borrowid "
                + "WHERE (b.sourceShopId = ? OR b.destinationShopId = ?) ";

        if (filterState != null && !filterState.isEmpty()) {
            sql += "AND b.state = ? ";
        } else {
            sql += "AND b.state != 'F' ";
        }
        sql += "GROUP BY b.id ";

        if (sortBy != null) {
            switch (sortBy) {
                case "date":
                    sql += "ORDER BY borrowDate DESC";
                    break;
                case "itemCount":
                    sql += "ORDER BY itemCount DESC";
                    break;
                case "status":
                    sql += "ORDER BY b.state";
                    break;
            }
        }

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setLong(paramIndex++, shopId); // 第一個參數
            stmt.setLong(paramIndex++, shopId); // 第二個參數

            if (filterState != null && !filterState.isEmpty()) {
                stmt.setString(paramIndex++, filterState); // 第三個參數（如果有）
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BorrowBean borrow = new BorrowBean();
                borrow.setBorrowId(rs.getLong("borrowId"));
                borrow.setBorrowDate(rs.getTimestamp("borrowDate"));
                borrow.setBorrowState(rs.getString("borrowState"));
                borrow.setSourceShopId(rs.getLong("fromId"));
                borrow.setDestinationShopId(rs.getLong("toId"));
                borrow.setSourceShopName(rs.getString("fromShop"));
                borrow.setDestinationShopName(rs.getString("toShop"));
                borrow.setItemCount(rs.getInt("itemCount"));
                BorrowRecords.add(borrow);
            }
        }
        return BorrowRecords;
    }

    public boolean updateRecordState(long recordId, String newState, long currentShopId) throws SQLException {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            // 獲取借用記錄的詳細資訊
            String checkSql = "SELECT sourceShopId, destinationShopId, state FROM borrow WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setLong(1, recordId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                return false;
            }

            long sourceShopId = rs.getLong("sourceShopId");
            long destShopId = rs.getLong("destinationShopId");
            String currentState = rs.getString("state");

            // 驗證權限
            if ("A".equals(newState) || "R".equals(newState)) {
                if (sourceShopId != currentShopId) {
                    return false;
                }
            } else if ("F".equals(newState)) {
                if (destShopId != currentShopId) {
                    return false;
                }
            } else {
                return false; // 無效的狀態轉換
            }

            // 驗證狀態轉換是否有效
            if ("C".equals(currentState) && ("A".equals(newState) || "R".equals(newState))) {
                // 允許從 C -> A 或 C -> R
            } else if ("A".equals(currentState) && "F".equals(newState)) {
                // 允許從 A -> F
            } else {
                return false; // 無效的狀態轉換
            }

            // 更新狀態
            String updateSql = "UPDATE borrow SET state = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, newState);
                updateStmt.setLong(2, recordId);
                updateStmt.executeUpdate();
            }

            // 根據新狀態調整庫存
            List<FruitBean> items = getBorrowDetails(recordId, conn);
            if ("A".equals(newState)) {
                // 扣減出貨商店的庫存，增加借貨商店的庫存
                for (FruitBean item : items) {
                    long fruitId = item.getId();
                    int num = item.getQuantity();

                    // 扣減出貨商店庫存
                    if (!updateFruitStock(conn, sourceShopId, fruitId, -num)) {
                        conn.rollback();
                        return false; // 如果庫存不足，回滾
                    }

                    // 增加借貨商店庫存
                    //updateFruitStock(conn, destShopId, fruitId, num);
                }
            } else if ("F".equals(newState)) {
                // 增加借貨商店的庫存
                for (FruitBean item : items) {
                    long fruitId = item.getId();
                    int num = item.getQuantity();
                    updateFruitStock(conn, destShopId, fruitId, num);
                }
            }

            conn.commit();
            return true;
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

    private boolean updateFruitStock(Connection conn, long shopId, long fruitId, int quantityChange) throws SQLException {
        // 檢查庫存是否足夠
        String checkSql = "SELECT num FROM shopStock WHERE shopid = ? AND fruitid = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setLong(1, shopId);
            checkStmt.setLong(2, fruitId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int currentStock = rs.getInt("num");
                if (currentStock + quantityChange < 0) {
                    // 如果更新後的數量小於 0，返回失敗
                    return false;
                }
            } else {
                // 如果沒有找到對應的庫存記錄，返回失敗
                return false;
            }
        }

        // 更新庫存數量
        String upsertSql = "INSERT INTO shopStock (shopid, fruitid, num) "
                + "VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE num = num + VALUES(num)";
        try (PreparedStatement updateStmt = conn.prepareStatement(upsertSql)) {
            updateStmt.setLong(1, shopId);
            updateStmt.setLong(2, fruitId);
            updateStmt.setInt(3, quantityChange);

            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private List<FruitBean> getBorrowDetails(long recordId, Connection conn) throws SQLException {
        List<FruitBean> items = new ArrayList<>();
        String sql = "SELECT fruitid, num FROM borrowDetail WHERE borrowid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, recordId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FruitBean item = new FruitBean();
                item.setId(rs.getLong("fruitid"));
                item.setQuantity(rs.getInt("num"));

                items.add(item);
            }
        }
        return items;
    }

    public BorrowBean getBorrowDetails(long borrowId) throws SQLException {
        BorrowBean borrowDetails = null;
        String sql = "SELECT "
                + "b.id AS borrowId, b.DT AS borrowDate, b.state AS borrowState, "
                + "sourceShop.id AS sourceShopId, sourceShop.name AS sourceShopName, "
                + "destinationShop.id AS destinationShopId, destinationShop.name AS destinationShopName, "
                + "f.id AS fruitId, f.name AS fruitName, f.unit AS fruitUnit, bd.num AS fruitQuantity "
                + "FROM borrow b "
                + "INNER JOIN shop sourceShop ON b.sourceShopid = sourceShop.id "
                + "INNER JOIN shop destinationShop ON b.destinationShopid = destinationShop.id "
                + "INNER JOIN borrowDetail bd ON b.id = bd.borrowid "
                + "INNER JOIN fruit f ON bd.fruitid = f.id "
                + "WHERE b.id = ?";

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, borrowId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (borrowDetails == null) {
                    borrowDetails = new BorrowBean();
                    borrowDetails.setBorrowId(rs.getLong("borrowId"));
                    borrowDetails.setBorrowDate(rs.getTimestamp("borrowDate"));
                    borrowDetails.setBorrowState(rs.getString("borrowState"));
                    borrowDetails.setSourceShopId(rs.getLong("sourceShopId"));
                    borrowDetails.setSourceShopName(rs.getString("sourceShopName"));
                    borrowDetails.setDestinationShopId(rs.getLong("destinationShopId"));
                    borrowDetails.setDestinationShopName(rs.getString("destinationShopName"));
                }

                // 添加水果詳細資訊
                FruitBean fruit = new FruitBean();
                fruit.setId(rs.getLong("fruitId"));
                fruit.setName(rs.getString("fruitName"));
                fruit.setUnit(rs.getString("fruitUnit"));
                fruit.setQuantity(rs.getInt("fruitQuantity"));

                borrowDetails.getFruits().add(fruit);
            }
        }

        return borrowDetails;
    }
}
