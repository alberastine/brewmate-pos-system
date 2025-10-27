package com.example.brewmate.models;

import org.json.JSONArray;

public class Receipt {
    private String receiptId;
    private JSONArray items;
    private double subtotal;
    private double tax;
    private double total;
    private String cashier;
    private String dateTime;

    // Constructor
    public Receipt(String receiptId, JSONArray items, double subtotal, double tax,
                   double total, String cashier, String dateTime) {
        this.receiptId = receiptId;
        this.items = items;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.cashier = cashier;
        this.dateTime = dateTime;
    }

    // Getters
    public String getReceiptId() { return receiptId; }
    public JSONArray getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public String getCashier() { return cashier; }
    public String getDateTime() { return dateTime; }

    // Setters (optional)
    public void setReceiptId(String receiptId) { this.receiptId = receiptId; }
    public void setItems(JSONArray items) { this.items = items; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public void setTax(double tax) { this.tax = tax; }
    public void setTotal(double total) { this.total = total; }
    public void setCashier(String cashier) { this.cashier = cashier; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
}
