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
        String sql = "SELECT r.id, r.DT, r.reserveDT, r.state, COUNT(rd.fruitid) as itemCount " +
                     "FROM reserve r " +
                     "JOIN reserveDetail rd ON r.id = rd.reserveid " +
                     "WHERE r.Shopid = ? AND r.state != 'F' ";
        
        if (filterState != null && !filterState.isEmpty()) {
            sql += "AND r.state = ? ";
        }
        sql += "GROUP BY r.id ";
        
        if (sortBy != null) {
            switch(sortBy) {
                case "date": sql += "ORDER BY r.DT DESC"; break;
                case "reserveDate": sql += "ORDER BY r.reserveDT"; break;
                case "status": sql += "ORDER BY r.state"; break;
            }
        }

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public boolean completeReservation(long reserveId) throws SQLException {
        String sql = "UPDATE reserve SET state = 'F' WHERE id = ? AND state = 'A'";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, reserveId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Map<String, Object> getReserveDetails(long reserveId) throws SQLException {
        Map<String, Object> details = new LinkedHashMap<>();
        String sql = "SELECT r.*, rd.fruitid, f.name, rd.num " +
                     "FROM reserve r " +
                     "JOIN reserveDetail rd ON r.id = rd.reserveid " +
                     "JOIN fruit f ON rd.fruitid = f.id " +
                     "WHERE r.id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
