package com.example.brewmate.models;

public class ProductSupply {
    private String productId;
    private String supplyId;
    private String supplyName; // Cached for display purposes
    private double quantityRequired; // Amount of supply needed per product unit

    // Required for Gson
    public ProductSupply() {}

    public ProductSupply(String productId, String supplyId, String supplyName, double quantityRequired) {
        this.productId = productId;
        this.supplyId = supplyId;
        this.supplyName = supplyName;
        this.quantityRequired = quantityRequired;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getSupplyId() {
        return supplyId;
    }

    public String getSupplyName() {
        return supplyName;
    }

    public double getQuantityRequired() {
        return quantityRequired;
    }

    // Setters
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setSupplyId(String supplyId) {
        this.supplyId = supplyId;
    }

    public void setSupplyName(String supplyName) {
        this.supplyName = supplyName;
    }

    public void setQuantityRequired(double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }
}
