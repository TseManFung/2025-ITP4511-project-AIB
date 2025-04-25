package AIB.DL;

import AIB.Bean.ConsumptionBean;
import AIB.Bean.ReserveReportBean;
import AIB.db.ITP4511_DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDL {
    private final ITP4511_DB db;
    
    public ReportDL(ITP4511_DB db) {
        this.db = db;
    }
    
    public List<ReserveReportBean> getReserveReport(String locationType, Long locationId) throws SQLException {
        List<ReserveReportBean> report = new ArrayList<>();
        String sql = "SELECT l.name AS location_name, f.name AS fruit_name, " +
                     "SUM(rd.num) AS total_reserved, MAX(r.reserveDT) AS latest_date " +
                     "FROM reserve r " +
                     "JOIN reserveDetail rd ON r.id = rd.reserveid " +
                     "JOIN shop s ON r.Shopid = s.id " +
                     "JOIN city c ON s.cityid = c.id " +
                     "JOIN country co ON c.countryid = co.id " +
                     "JOIN fruit f ON rd.fruitid = f.id " +
                     "JOIN (";
        
        if(locationType.equals("shop")) {
            sql += "SELECT s.id, s.name FROM shop s WHERE s.id = ?) l ON s.id = l.id ";
        } else if(locationType.equals("city")) {
            sql += "SELECT c.id, c.name FROM city c WHERE c.id = ?) l ON c.id = l.id ";
        } else {
            sql += "SELECT co.id, co.name FROM country co WHERE co.id = ?) l ON co.id = l.id ";
        }
        
        sql += "GROUP BY l.name, f.name";
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, locationId);
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()) {
                ReserveReportBean bean = new ReserveReportBean();
                bean.setLocationName(rs.getString("location_name"));
                bean.setLocationType(locationType.toUpperCase());
                bean.setFruitName(rs.getString("fruit_name"));
                bean.setTotalReserved(rs.getInt("total_reserved"));
                bean.setLatestReserveDate(rs.getTimestamp("latest_date"));
                report.add(bean);
            }
        }
        return report;
    }

    public List<ConsumptionBean> getConsumptionReport(String locationType, Long locationId) throws SQLException {
    List<ConsumptionBean> report = new ArrayList<>();
    String sql = "SELECT "
        + "CASE "
        + "WHEN MONTH(c.DT) BETWEEN 1 AND 3 THEN 'Winter' "
        + "WHEN MONTH(c.DT) BETWEEN 4 AND 6 THEN 'Spring' "
        + "WHEN MONTH(c.DT) BETWEEN 7 AND 9 THEN 'Summer' "
        + "ELSE 'Autumn' END AS season, "
        + "f.name AS fruit_name, SUM(cd.num) AS total_consumed, l.name AS location_name "
        + "FROM consume c "
        + "JOIN consumeDetail cd ON c.id = cd.consumeid "
        + "JOIN fruit f ON cd.fruitid = f.id "
        + "JOIN shop s ON c.shopid = s.id "
        + "JOIN city ci ON s.cityid = ci.id "
        + "JOIN country co ON ci.countryid = co.id "
        + "JOIN (";

    if(locationType.equals("shop")) {
        sql += "SELECT s.id, s.name FROM shop s WHERE s.id = ?) l ON s.id = l.id ";
    } else if(locationType.equals("city")) {
        sql += "SELECT ci.id, ci.name FROM city ci WHERE ci.id = ?) l ON ci.id = l.id ";
    } else {
        sql += "SELECT co.id, co.name FROM country co WHERE co.id = ?) l ON co.id = l.id ";
    }

    sql += "GROUP BY season, f.name, l.name";

    try (Connection conn = db.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setLong(1, locationId);
        ResultSet rs = stmt.executeQuery();
        
        while(rs.next()) {
            ConsumptionBean bean = new ConsumptionBean();
            bean.setSeason(rs.getString("season"));
            bean.setFruitName(rs.getString("fruit_name"));
            bean.setTotalConsumed(rs.getInt("total_consumed"));
            bean.setLocationName(rs.getString("location_name"));
            bean.setLocationType(locationType.toUpperCase());
            report.add(bean);
        }
    }
    return report;
}
}