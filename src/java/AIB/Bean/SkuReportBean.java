package AIB.Bean;

import java.io.Serializable;

public class SkuReportBean implements Serializable {
    private String countryName;
    private Long countryId;
    private String fruitName;
    private Long avgDeliveryDays;
    private Long totalNeeded;
    private boolean stockAvailable;

    // Default constructor
    public SkuReportBean() {
    }

    // Getters and Setters
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getFruitName() {
        return fruitName;
    }

    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }

    public Long getAvgDeliveryDays() {
        return avgDeliveryDays;
    }

    public void setAvgDeliveryDays(Long avgDeliveryDays) {
        this.avgDeliveryDays = avgDeliveryDays;
    }

    public Long getTotalNeeded() {
        return totalNeeded;
    }

    public void setTotalNeeded(Long totalNeeded) {
        this.totalNeeded = totalNeeded;
    }

    public boolean isStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(boolean stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    @Override
    public String toString() {
        return "SkuReportBean{" +
               "countryName='" + countryName + '\'' +
               ", countryId=" + countryId +
               ", fruitName='" + fruitName + '\'' +
               ", avgDeliveryDays=" + avgDeliveryDays +
               ", totalNeeded=" + totalNeeded +
               ", stockAvailable=" + stockAvailable +
               '}';
    }
}