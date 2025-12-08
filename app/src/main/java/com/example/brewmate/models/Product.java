package com.example.brewmate.models;

public class Product {
    private String id;
    private String name;
    private double price;
    private String category;
    private int quantity;
    private java.util.List<ProductSupply> supplies;

    public Product(String id, String name, double price, String category) {
        this(id, name, price, category, new java.util.ArrayList<>());
    }

    public Product(String id, String name, double price, String category,
                   java.util.List<ProductSupply> supplies) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.supplies = supplies;
        this.quantity = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public java.util.List<ProductSupply> getSupplies() {
        return supplies;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSupplies(java.util.List<ProductSupply> supplies) {
        this.supplies = supplies;
    }
}
