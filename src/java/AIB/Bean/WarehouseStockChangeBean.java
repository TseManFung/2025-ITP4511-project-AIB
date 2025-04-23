package AIB.Bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class WarehouseStockChangeBean implements Serializable {
    private Long id;
    private Timestamp deliveryStartTime;
    private Timestamp deliveryEndTime;
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;
    private String state; // C, A, R, F
    private Long fruitId;
    private Integer num;

    // Default constructor
    public WarehouseStockChangeBean() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDeliveryStartTime() {
        return deliveryStartTime;
    }

    public void setDeliveryStartTime(Timestamp deliveryStartTime) {
        this.deliveryStartTime = deliveryStartTime;
    }

    public Timestamp getDeliveryEndTime() {
        return deliveryEndTime;
    }

    public void setDeliveryEndTime(Timestamp deliveryEndTime) {
        this.deliveryEndTime = deliveryEndTime;
    }

    public Long getSourceWarehouseId() {
        return sourceWarehouseId;
    }

    public void setSourceWarehouseId(Long sourceWarehouseId) {
        this.sourceWarehouseId = sourceWarehouseId;
    }

    public Long getDestinationWarehouseId() {
        return destinationWarehouseId;
    }

    public void setDestinationWarehouseId(Long destinationWarehouseId) {
        this.destinationWarehouseId = destinationWarehouseId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getFruitId() {
        return fruitId;
    }

    public void setFruitId(Long fruitId) {
        this.fruitId = fruitId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "WarehouseStockChangeBean{" +
               "id=" + id +
               ", deliveryStartTime=" + deliveryStartTime +
               ", deliveryEndTime=" + deliveryEndTime +
               ", sourceWarehouseId=" + sourceWarehouseId +
               ", destinationWarehouseId=" + destinationWarehouseId +
               ", state='" + state + '\'' +
               ", fruitId=" + fruitId +
               ", num=" + num +
               '}';
    }
}