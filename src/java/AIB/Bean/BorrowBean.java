/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package AIB.Bean;

import java.beans.*;
import java.io.Serializable;
import java.sql.Timestamp;
/**
 *
 * @author andyt
 */
import java.util.ArrayList;

public class BorrowBean implements Serializable {

    /*
     * SELECT
     * b.id AS borrowId,
     * b.DT AS borrowDate,
     * b.state AS borrowState,
     * sourceShop.id AS sourceShopId,
     * sourceShop.name AS sourceShopName,
     * destinationShop.id AS destinationShopId,
     * destinationShop.name AS destinationShopName,
     * f.id AS fruitId,
     * f.name AS fruitName,
     * f.unit AS fruitUnit,
     * bd.num AS fruitQuantity
     * FROM
     * borrow b
     * INNER JOIN shop sourceShop ON b.sourceShopid = sourceShop.id
     * INNER JOIN shop destinationShop ON b.destinationShopid = destinationShop.id
     * INNER JOIN borrowDetail bd ON b.id = bd.borrowid
     * INNER JOIN fruit f ON bd.fruitid = f.id;
     */

    private Long borrowId;
    private Timestamp borrowDate;
    private String borrowState;
    private Long sourceShopId;//出貨商店ID
    private String sourceShopName;
    private Long destinationShopId;//借貨商店ID
    private String destinationShopName;
    private ArrayList<FruitBean> fruits = new ArrayList<>();
    private int itemCount = 0;

    public int getItemCount() {
        return this.itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
    // Item count for the borrow record

    public Long getBorrowId() {
        return this.borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
    }

    public Timestamp getBorrowDate() {
        return this.borrowDate;
    }

    public void setBorrowDate(Timestamp borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getBorrowState() {
        return this.borrowState;
    }

    public void setBorrowState(String borrowState) {
        this.borrowState = borrowState;
    }

    public Long getSourceShopId() {
        return this.sourceShopId;
    }

    public void setSourceShopId(Long sourceShopId) {
        this.sourceShopId = sourceShopId;
    }

    public String getSourceShopName() {
        return this.sourceShopName;
    }

    public void setSourceShopName(String sourceShopName) {
        this.sourceShopName = sourceShopName;
    }

    public Long getDestinationShopId() {
        return this.destinationShopId;
    }

    public void setDestinationShopId(Long destinationShopId) {
        this.destinationShopId = destinationShopId;
    }

    public String getDestinationShopName() {
        return this.destinationShopName;
    }

    public void setDestinationShopName(String destinationShopName) {
        this.destinationShopName = destinationShopName;
    }

    public ArrayList<FruitBean> getFruits() {
        return this.fruits;
    }

    public void setFruits(ArrayList<FruitBean> fruits) {
        this.fruits = fruits;
    }

    public BorrowBean() {
    }

    public BorrowBean(Long borrowId, Timestamp borrowDate, String borrowState, Long sourceShopId, String sourceShopName,
            Long destinationShopId, String destinationShopName) {
        this.borrowId = borrowId;
        this.borrowDate = borrowDate;
        this.borrowState = borrowState;
        this.sourceShopId = sourceShopId;
        this.sourceShopName = sourceShopName;
        this.destinationShopId = destinationShopId;
        this.destinationShopName = destinationShopName;

    }
}
