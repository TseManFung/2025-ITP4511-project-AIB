/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package AIB.Bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author andyt
 */
public class WarehouseBean implements Serializable {
    private Long warehouseId;
    private String warehouseName;
    private String type; // 'S' (source) or 'C' (central)
    private Long countryId;
    private String countryName;
    private ArrayList<WarehouseFruitStock> fruits = new ArrayList<>();

    public WarehouseBean() {
    }

    public WarehouseBean(Long warehouseId, String warehouseName, String type, Long countryId, String countryName) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.type = type;
        this.countryId = countryId;
        this.countryName = countryName;
    }

    // Getters and Setters
    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public ArrayList<WarehouseFruitStock> getFruits() {
        return fruits;
    }

    public void setFruits(ArrayList<WarehouseFruitStock> fruits) {
        this.fruits = fruits;
    }

    // Inner class for warehouse fruit stock details
    public static class WarehouseFruitStock implements Serializable {
        private Long fruitId;
        private String fruitName;
        private String unit;
        private int quantity;

        public Long getFruitId() {
            return fruitId;
        }

        public void setFruitId(Long fruitId) {
            this.fruitId = fruitId;
        }

        public String getFruitName() {
            return fruitName;
        }

        public void setFruitName(String fruitName) {
            this.fruitName = fruitName;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
