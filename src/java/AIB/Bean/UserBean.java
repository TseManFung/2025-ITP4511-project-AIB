/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AIB.Bean;

import java.io.Serializable;

public class UserBean implements Serializable {

    private String loginName;
    private String name;
    private String password;
    private char type; // B, W, S, D
    private Long warehouseId;
    private Long shopId;

    // Default constructor
    public UserBean() {
    }

    // Parameterized constructor
    public UserBean(String loginName, String name, String password, char type, Long warehouseId, Long shopId) {
        this.loginName = loginName;
        this.name = name;
        this.password = password;
        this.type = type;
        this.warehouseId = warehouseId;
        this.shopId = shopId;
    }

    // Getters and Setters
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public Long getWarehouseId() {
        return warehouseId != null && warehouseId > 0 ? warehouseId : null;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getShopId() {
        return shopId != null && shopId > 0 ? shopId : null;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    // Helper method to get user type as full description
    public String getTypeDescription() {
        switch (this.type) {
            case 'B':
                return "Bakery Shop Staff";
            case 'W':
                return "Warehouse Staff";
            case 'S':
                return "Senior Management";
            case 'D':
                return "Deleted User";
            default:
                return "Unknown";
        }
    }

    // Override toString() for debugging purposes
    @Override
    public String toString() {
        return "UserBean{"
                + "loginName='" + loginName + '\''
                + ", name='" + name + '\''
                + ", password='[PROTECTED]'"
                + ", type=" + type
                + ", warehouseId=" + warehouseId
                + ", shopId=" + shopId
                + '}';
    }
}
