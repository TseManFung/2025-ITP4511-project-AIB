package AIB.Bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class ConsumptionBean implements Serializable {
    private String season;
    private String fruitName;
    private int totalConsumed;
    private String locationName;
    private String locationType;
    private int year;

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ConsumptionBean() {}
    
    // Getters and Setters
    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
    public String getFruitName() { return fruitName; }
    public void setFruitName(String fruitName) { this.fruitName = fruitName; }
    public int getTotalConsumed() { return totalConsumed; }
    public void setTotalConsumed(int totalConsumed) { this.totalConsumed = totalConsumed; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }
}