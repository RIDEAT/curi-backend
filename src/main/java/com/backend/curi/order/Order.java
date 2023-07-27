package com.backend.curi.order;

public class Order {

    public Order(int id,String item) {

        this.item=item;

        this.id = id;

    }

    public String getItem() {

        return item;

    }

    public void setItem(String item) {

        this.item = item;

    }

    public int getPricePerItem() {

        return pricePerItem;

    }

    public void setPricePerItem(int pricePerItem) {

        this.pricePerItem = pricePerItem;

    }

    public int getQuantity() {

        return quantity;

    }

    public void setQuantity(int quantity) {

        this.quantity = quantity;

    }

    public int getTotalPrice() {

        return totalPrice;

    }

    public void setTotalPrice(int totalPrice) {

        this.totalPrice = totalPrice;

    }

    String item;

    int pricePerItem;

    int quantity;

    int totalPrice;

    int id;

    String user;

    public String getUser() {

        return user;

    }

    public void setUser(String user) {

        this.user = user;

    }

    public int getId() {

        return id;

    }

    public void setId(int id) {

        this.id = id;

    }



}
