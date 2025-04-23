package AIB.Bean;

import java.io.Serializable;

public class StockBean implements Serializable{
    private String fruitId;
    private String fruitName;
    private int currentStock;
    private String warehouseId;
    
    public StockBean(String fruitId, String fruitName, int currentStock, String warehouseId) {
        this.fruitId = fruitId;
        this.fruitName = fruitName;
        this.currentStock = currentStock;
        this.warehouseId = warehouseId;
    }

    // Getters
    public String getFruitId() { return fruitId; }
    public String getFruitName() { return fruitName; }
    public int getCurrentStock() { return currentStock; }
    public String getWarehouseId() { return warehouseId; }
}