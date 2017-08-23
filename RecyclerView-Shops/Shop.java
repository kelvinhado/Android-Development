package com.kelvinhado.recyclerview;

/**
 * Created by kelvin on 23/08/2017.
 */

public class Shop {

    private String shopName;
    private double shopRate;

    public Shop(String shopName, double shopRate) {
        this.shopName = shopName;
        this.shopRate = shopRate;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public double getShopRate() {
        return shopRate;
    }

    public void setShopRate(double shopRate) {
        this.shopRate = shopRate;
    }
}
