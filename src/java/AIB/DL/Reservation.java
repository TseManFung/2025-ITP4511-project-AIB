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
import AIB.algorithm.SnowflakeSingleton;
import AIB.db.ITP4511_DB;

/**
 *
 * @author andyt
 */
public class Reservation implements Serializable {

    private ITP4511_DB db;

    public Reservation(ITP4511_DB db) {
        this.db = db;
    }

    public List<FruitBean> getAvailableFruits() throws SQLException {
        List<FruitBean> fruits = new ArrayList<>();
        String sql = "SELECT f.id, f.name, (SUM(ws.num) - COALESCE(rd.num, 0)) as available, unit "
            + "FROM fruit f "
            + "INNER JOIN warehouse w ON (w.type = 'S' OR w.countryid = 1) "
            + "INNER JOIN warehouseStock ws ON ws.warehouseid = w.id AND f.id = ws.fruitid "
            + "LEFT JOIN (SELECT fruitid, SUM(num) as num FROM reserve r "
            + "INNER JOIN reserveDetail rd ON rd.reserveid = r.id "
            + "WHERE r.state = 'C' "
            + "GROUP BY fruitid) rd ON f.id = rd.fruitid "
            + "GROUP BY f.id";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FruitBean fruit = new FruitBean();
                fruit.setId(rs.getLong("id"));
                fruit.setName(rs.getString("name"));
                fruit.setQuantity(rs.getInt("available"));
                fruit.setUnit(rs.getString("unit"));
                fruits.add(fruit);
            }
        }
        return fruits;
    }

    public long createReservation(long shopId, Map<Long, Integer> items, String reserveDT) throws SQLException {
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);

            long reserveId = SnowflakeSingleton.getInstance().nextId();

            // Create reserve record
            String reserveSql = "INSERT INTO reserve (id, Shopid, reserveDT, state) VALUES (?, ?, ?, 'C')";
            try (PreparedStatement stmt = conn.prepareStatement(reserveSql)) {
                stmt.setLong(1, reserveId);
                stmt.setLong(2, shopId);
                stmt.setTimestamp(3, Timestamp.valueOf(reserveDT + " 00:00:00")); // 將日期轉換為 Timestamp
                stmt.executeUpdate();
            }

            // Add reserve details
            String detailSql = "INSERT INTO reserveDetail (reserveid, fruitid, num) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(detailSql)) {
                for (Map.Entry<Long, Integer> entry : items.entrySet()) {
                    stmt.setLong(1, reserveId);
                    stmt.setLong(2, entry.getKey());
                    stmt.setInt(3, entry.getValue());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
            return reserveId;
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
