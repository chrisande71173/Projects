package com.techelevator.model;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDto {
    private List<CartItem> cartItems;
    private String subtotal;
    private String taxRate;
    private String taxAmount;
    private String total;

    // Constructor
    public CartResponseDto(List<CartItem> cartItems, String subtotal, String taxRate, String taxAmount, String total) {
        this.cartItems = cartItems;
        this.subtotal = subtotal;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.total = total;
    }

    // Getters and Setters
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
