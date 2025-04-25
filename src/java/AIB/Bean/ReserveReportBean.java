package AIB.Bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class ReserveReportBean implements Serializable {
    private String locationName;
    private String locationType;
    private String fruitName;
    private int totalReserved;
    private Timestamp latestReserveDate;

    public ReserveReportBean() {}
    
    // Getters and Setters
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }
    public String getFruitName() { return fruitName; }
    public void setFruitName(String fruitName) { this.fruitName = fruitName; }
    public int getTotalReserved() { return totalReserved; }
    public void setTotalReserved(int totalReserved) { this.totalReserved = totalReserved; }
    public Timestamp getLatestReserveDate() { return latestReserveDate; }
    public void setLatestReserveDate(Timestamp latestReserveDate) { this.latestReserveDate = latestReserveDate; }
}