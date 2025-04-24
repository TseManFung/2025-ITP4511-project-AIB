package AIB.Bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ReserveBean implements Serializable {
    private Long id;
    private Timestamp DT;
    private Long shopId;
    private Timestamp reserveDT;
    private String state; // C, A, R, F
    private Long fruitId;
    private Integer num;
    private ArrayList<FruitBean> fruits = new ArrayList<>();
    private int itemCount;

    public int getItemCount() {
        return this.itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public ArrayList<FruitBean> getFruits() {
        return this.fruits;
    }

    // Default constructor
    public ReserveBean() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getDT() {
        return DT;
    }

    public void setDT(Timestamp DT) {
        this.DT = DT;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Timestamp getReserveDT() {
        return reserveDT;
    }

    public void setReserveDT(Timestamp reserveDT) {
        this.reserveDT = reserveDT;
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
        return "ReserveBean{" +
               "id=" + id +
               ", DT=" + DT +
               ", shopId=" + shopId +
               ", reserveDT=" + reserveDT +
               ", state='" + state + '\'' +
               ", fruitId=" + fruitId +
               ", num=" + num +
               '}';
    }
}