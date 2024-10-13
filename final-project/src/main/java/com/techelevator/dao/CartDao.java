package com.techelevator.dao;

import com.techelevator.model.CartItem;
import java.util.List;

public interface CartDao {
    List<CartItem> getCartByUserId(int userId);
    void addCartItem(CartItem cartItem);
    void removeCartItem(int cartItemId);
    void clearCartByUserId(int userId);
    CartItem getCartItemById(int itemId);
    int getUserIdByUsername(String username);
    String getStateCodeByUserId(int userId);
}
