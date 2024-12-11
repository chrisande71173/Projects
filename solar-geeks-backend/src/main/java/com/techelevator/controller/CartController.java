package com.techelevator.controller;

import com.techelevator.dao.CartDao;
import com.techelevator.exception.DaoException;
import com.techelevator.model.CartItem;
import com.techelevator.model.CartResponseDto;
import com.techelevator.service.StateTaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartDao cartDao;
    private final StateTaxService stateTaxService;

    @Autowired
    public CartController(CartDao cartDao, StateTaxService stateTaxService) {
        this.cartDao = cartDao;
        this.stateTaxService = stateTaxService;
    }

    // Get cart items by user
    @GetMapping
    public CartResponseDto getCart(@Valid Principal principal) {
        try {
            String username = principal.getName();
            int userId = cartDao.getUserIdByUsername(username); // Fetch user ID based on the username

            // Fetch user's state code from the user table
            String stateCode = cartDao.getStateCodeByUserId(userId);

            // Get cart items for the current user
            List<CartItem> cartItems = cartDao.getCartByUserId(userId);

            // Check if all items in the cart belong to the current user
            for (CartItem item : cartItems) {
                if (item.getUserId() != userId) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cart contains items not belonging to the user.");
                }
            }

            // Calculate subtotal
            BigDecimal subtotal = cartItems.stream()
                    .map(CartItem::getTotalPrice) // Applies the getTotalPrice method to each CartItem object in the stream.
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // Iterates through each total price value in the stream adding each value to the accumulated result starting from zero.

            // Get tax rate based on the user's state code
            BigDecimal taxRate = stateTaxService.getTaxRate(stateCode);

            // Format tax rate as a percentage with 2 decimal places
            String formattedTaxRate = formatTaxRateAsPercentage(taxRate);

            // Calculate tax amount
            BigDecimal taxAmount = subtotal.multiply(taxRate);

            // Format tax amount for display with 2 decimal places
            String formattedTaxAmount = formatBigDecimalForDisplay(taxAmount);

            // Calculate total with tax
            BigDecimal totalWithTax = subtotal.add(taxAmount);

            // Format subtotal and total with tax for display with 2 decimal places
            String formattedSubtotal = formatBigDecimalForDisplay(subtotal);
            String formattedTotalWithTax = formatBigDecimalForDisplay(totalWithTax);

            // Return the response with formatted values and tax amount
            return new CartResponseDto(cartItems, formattedSubtotal, formattedTaxRate, formattedTaxAmount, formattedTotalWithTax);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve cart information", e);
        }
    }

    private String formatBigDecimalForDisplay(BigDecimal value) {
        return String.format("$%.2f", value); // Formats the BigDecimal value to a string with 2 decimal places and a "$" sign
    }

    private String formatTaxRateAsPercentage(BigDecimal taxRate) {
        // Convert tax rate to percentage and format with 2 decimal places and a "%" sign
        return String.format("%.2f%%", taxRate.multiply(BigDecimal.valueOf(100)));
    }

    // Add item to cart
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItem(@Valid Principal principal, @RequestBody CartItem cartItem) {
        try {
            String username = principal.getName();
            int userId = cartDao.getUserIdByUsername(username); // Fetch user ID based on the username

            // Set the user ID in the CartItem to the current user's ID
            cartItem.setUserId(userId);

            // Check if the quantity is greater than zero
            if (cartItem.getQuantity() > 0) {
                cartDao.addCartItem(cartItem); // Add or update the cart item
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add item to cart", e);
        }
    }

    // Remove item from cart
    @DeleteMapping("/items/{itemId}")
    public void removeItem(@Valid Principal principal, @PathVariable int itemId) {
        try {
            String username = principal.getName();
            int userId = cartDao.getUserIdByUsername(username); // Fetch user ID based on the username
            CartItem cartItem = cartDao.getCartItemById(itemId);

            if (cartItem == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found.");
            }

            if (cartItem.getUserId() == userId) {
                cartDao.removeCartItem(itemId);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to remove this item.");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove item from cart", e);
        }
    }

    // Clear the cart
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@Valid Principal principal) {
        try {
            // Get the username of the authenticated user
            String username = principal.getName();
            int userId = cartDao.getUserIdByUsername(username); // Fetch user ID based on the username

            // Check if the user owns the cart and if the cart contains items
            List<CartItem> cartItems = cartDao.getCartByUserId(userId); // Retrieve all cart items for the user

            // Clear the cart if the user owns the cart and it has items
            if (!cartItems.isEmpty()) {
                cartDao.clearCartByUserId(userId); // Clear only if user owns the cart
            } else {
                // If the cart is empty or does not belong to the user, throw forbidden
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to clear this cart.");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to clear cart", e);
        }
    }

}