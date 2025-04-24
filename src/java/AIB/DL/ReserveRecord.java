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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import AIB.db.ITP4511_DB;

/**
 *
 * @author andyt
 */
public class ReserveRecordBean implements Serializable {

    private final ITP4511_DB db;

    public ReserveRecordBean(ITP4511_DB db) {
        this.db = db;
    }

    public List<Map<String, Object>> getReserveRecords(long shopId, String filterState, String sortBy) throws SQLException {
        List<Map<String, Object>> records = new ArrayList<>();
        String sql = "SELECT r.id, r.DT, r.reserveDT, r.state, COUNT(rd.fruitid) as itemCount "
                + "FROM reserve r "
                + "JOIN reserveDetail rd ON r.id = rd.reserveid "
                + "WHERE r.Shopid = ? ";

        if (filterState != null && !filterState.isEmpty()) {
            sql += "AND r.state = ? ";
        }else {
            sql += "AND r.state != 'F' ";
        }
        sql += "GROUP BY r.id ";

        if (sortBy != null) {
            switch (sortBy) {
                case "date":
                    sql += "ORDER BY r.DT DESC";
                    break;
                case "reserveDate":
                    sql += "ORDER BY r.reserveDT";
                    break;
                case "status":
                    sql += "ORDER BY r.state";
                    break;
            }
        }

        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            stmt.setLong(paramIndex++, shopId);
            if (filterState != null && !filterState.isEmpty()) {
                stmt.setString(paramIndex, filterState);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> record = new LinkedHashMap<>();
                record.put("id", rs.getLong("id"));
                record.put("DT", rs.getTimestamp("DT"));
                record.put("reserveDT", rs.getTimestamp("reserveDT"));
                record.put("state", rs.getString("state"));
                record.put("itemCount", rs.getInt("itemCount"));
                records.add(record);
            }
        }
        return records;
    }

    public boolean completeReservation(long reserveId,long shopId) throws SQLException {
        String updateReserveSql = "UPDATE reserve SET state = 'F' WHERE id = ? AND state = 'A'";
        String updateStockSql = "INSERT INTO shopStock (shopid, fruitid, num) " +
                                 "VALUES (?, ?, ?) " +
                                 "ON DUPLICATE KEY UPDATE num = num + VALUES(num)";
        String getReserveDetailsSql = "SELECT fruitid, num FROM reserveDetail WHERE reserveid = ?";
    
        Connection conn = null;
        try {
            conn = db.getConnection();
            conn.setAutoCommit(false); 
    
            try (PreparedStatement stmt = conn.prepareStatement(updateReserveSql)) {
                stmt.setLong(1, reserveId);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated == 0) {
                    conn.rollback();
                    return false;
                }
            }
    
            try (PreparedStatement stmt = conn.prepareStatement(getReserveDetailsSql)) {
                stmt.setLong(1, reserveId);
                ResultSet rs = stmt.executeQuery();
                try (PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql)) {
                    while (rs.next()) {
                        long fruitId = rs.getLong("fruitid");
                        int num = rs.getInt("num");
    
                        updateStockStmt.setLong(1, shopId);
                        updateStockStmt.setLong(2, fruitId);
                        updateStockStmt.setInt(3, num);
                        updateStockStmt.addBatch();
                    }
                    updateStockStmt.executeBatch();
                }
            }
    
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); 
            }
            System.err.println("Error completing reservation: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); 
                conn.close();
            }
        }
    }

    public Map<String, Object> getReserveDetails(long reserveId) throws SQLException {
        Map<String, Object> details = new LinkedHashMap<>();
        String sql = "SELECT r.*, rd.fruitid, f.name, rd.num "
                + "FROM reserve r "
                + "JOIN reserveDetail rd ON r.id = rd.reserveid "
                + "JOIN fruit f ON rd.fruitid = f.id "
                + "WHERE r.id = ?";
        try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, reserveId);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> items = new ArrayList<>();
            while (rs.next()) {
                if (details.isEmpty()) {
                    details.put("id", rs.getLong("id"));
                    details.put("DT", rs.getTimestamp("DT"));
                    details.put("reserveDT", rs.getTimestamp("reserveDT"));
                    details.put("state", rs.getString("state"));
                }
                Map<String, Object> item = new HashMap<>();
                item.put("fruitId", rs.getLong("fruitid"));
                item.put("fruitName", rs.getString("name"));
                item.put("quantity", rs.getInt("num"));
                items.add(item);
            }
            details.put("items", items);
        }
        return details;
    }
}
