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
import AIB.Bean.ReserveBean;
import AIB.db.ITP4511_DB;

/**
 *
 * @author andyt
 */
public class ReserveRecord implements Serializable {

    private ITP4511_DB db;

    public ReserveRecord(ITP4511_DB db) {
        this.db = db;
    }

    public List<ReserveBean> getReserveRecords(long shopId, String filterState, String sortBy) throws SQLException {
        List<ReserveBean> reserveRecords = new ArrayList<>();
        String sql = "SELECT r.id, r.DT, r.reserveDT, r.state, COUNT(rd.fruitid) as itemCount "
                   + "FROM reserve r "
                   + "JOIN reserveDetail rd ON r.id = rd.reserveid "
                   + "WHERE r.Shopid = ? ";
    
        if (filterState != null && !filterState.isEmpty()) {
            sql += "AND r.state = ? ";
        } else {
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
                ReserveBean reserveBean = new ReserveBean();
                reserveBean.setId(rs.getLong("id"));
                reserveBean.setDT(rs.getTimestamp("DT"));
                reserveBean.setReserveDT(rs.getTimestamp("reserveDT"));
                reserveBean.setState(rs.getString("state"));
                reserveBean.setItemCount(rs.getInt("itemCount"));
    
                reserveRecords.add(reserveBean);
            }
        }
        return reserveRecords;
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

    public ReserveBean getReserveDetails(long reserveId) throws SQLException {
    ReserveBean reserveDetails = new ReserveBean();
    String sql = "SELECT r.id, r.DT, r.reserveDT, r.state, r.Shopid, rd.fruitid, rd.num, f.name, f.unit "
               + "FROM reserve r "
               + "JOIN reserveDetail rd ON r.id = rd.reserveid "
               + "JOIN fruit f ON rd.fruitid = f.id "
               + "WHERE r.id = ?";
    try (Connection conn = db.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setLong(1, reserveId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            if(reserveDetails.getId() == null) {
                reserveDetails.setId(rs.getLong("id"));
                reserveDetails.setDT(rs.getTimestamp("DT"));
                reserveDetails.setReserveDT(rs.getTimestamp("reserveDT"));
                reserveDetails.setState(rs.getString("state"));
                reserveDetails.setShopId(rs.getLong("Shopid"));
            }

            FruitBean fruit = new FruitBean();
            fruit.setId(rs.getLong("fruitid"));
            fruit.setName(rs.getString("name"));
            fruit.setUnit(rs.getString("unit"));
            fruit.setQuantity(rs.getInt("num"));

            reserveDetails.getFruits().add(fruit);
        }
    }
    return reserveDetails;
}
}
