package com.example.brewmate.models;

public class Supply {
    private String id;
    private String supplierName;
    private String supplyName;
    private String quantity;

    public Supply(String id, String supplierName, String supplyName, String quantity) {
        this.id = id;
        this.supplierName = supplierName;
        this.supplyName = supplyName;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getSupplierName() { return supplierName; }
    public String getSupplyName() { return supplyName; }
    public String getQuantity() { return quantity; }


    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public void setSupplyName(String supplyName) { this.supplyName = supplyName; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
}
