/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Beans/Bean.java to edit this template
 */
package AIB.Bean;

import java.beans.*;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import AIB.db.ITP4511_DB;

/**
 *
 * @author andyt
 */
public class ShopBean implements Serializable {
    /* SELECT 
    shop.id AS shopId,
    shop.name AS shopName,
    city.id AS cityId,
    city.name AS cityName,
    country.id AS countryId,
    country.name AS countryName,
    shopStock.fruitid AS fruitId,
    fruit.name AS fruitName,
    shopStock.num AS stockQuantity
FROM 
    shop
INNER JOIN 
    city ON shop.cityid = city.id
INNER JOIN 
    country ON city.countryid = country.id
INNER JOIN 
    shopStock ON shop.id = shopStock.shopid
INNER JOIN 
    fruit ON shopStock.fruitid = fruit.id; */
    private Long Shopid;
    private String ShopName;
    private Long Cityid;
    private String CityName;
    private Long Countryid;
    private String CountryName;
    private ArrayList<FruitBean> fruits = new ArrayList<>();

    public Long getShopid() {
        return this.Shopid;
    }

    public void setShopid(Long Shopid) {
        this.Shopid = Shopid;
    }

    public String getShopName() {
        return this.ShopName;
    }

    public void setShopName(String ShopName) {
        this.ShopName = ShopName;
    }

    public Long getCityid() {
        return this.Cityid;
    }

    public void setCityid(Long Cityid) {
        this.Cityid = Cityid;
    }

    public String getCityName() {
        return this.CityName;
    }

    public void setCityName(String CityName) {
        this.CityName = CityName;
    }

    public Long getCountryid() {
        return this.Countryid;
    }

    public void setCountryid(Long Countryid) {
        this.Countryid = Countryid;
    }

    public String getCountryName() {
        return this.CountryName;
    }

    public void setCountryName(String CountryName) {
        this.CountryName = CountryName;
    }

    public ArrayList<FruitBean> getFruits() {
        return this.fruits;
    }

    public ShopBean() {
        
    }
   
    public ShopBean(Long Shopid, String ShopName, Long Cityid, String CityName, Long Countryid, String CountryName) {
        this.Shopid = Shopid;
        this.ShopName = ShopName;
        this.Cityid = Cityid;
        this.CityName = CityName;
        this.Countryid = Countryid;
        this.CountryName = CountryName;
    }
    
}
