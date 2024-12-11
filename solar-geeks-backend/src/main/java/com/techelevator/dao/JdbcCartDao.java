package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.CartItem;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcCartDao implements CartDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcCartDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<CartItem> getCartByUserId(int userId) {
        String sql = "SELECT ci.cart_item_id, ci.user_id, p.product_id, p.name AS product_name, ci.quantity, p.price, (ci.quantity * p.price) AS total_price " +
                "FROM cart_item ci " +
                "JOIN product p ON ci.product_id = p.product_id " +
                "WHERE ci.user_id = ?";
        List<CartItem> cartItems = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            while (results.next()) {
                cartItems.add(mapRowToCartItem(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return cartItems;
    }

    @Override
    public void addCartItem(CartItem cartItem) {
        // Check if the product is already in the user's cart
        if (cartItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        if (cartItemExists(cartItem.getUserId(), cartItem.getProductId())) {
            // If the item exists, update the quantity
            updateCartItemQuantity(cartItem);
        } else {
            // If it doesn't exist, insert a new item
            insertCartItem(cartItem);
        }
    }

    private void insertCartItem(CartItem cartItem) {
        String sql = "INSERT INTO cart_item (user_id, product_id, quantity) VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, cartItem.getUserId(), cartItem.getProductId(), cartItem.getQuantity());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    private void updateCartItemQuantity(CartItem cartItem) {
        String sql = "UPDATE cart_item SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try {
            jdbcTemplate.update(sql, cartItem.getQuantity(), cartItem.getUserId(), cartItem.getProductId());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    public boolean cartItemExists(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM cart_item WHERE user_id = ? AND product_id = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, productId);
            return count != null && count > 0;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public void removeCartItem(int cartItemId) {
        String sql = "DELETE FROM cart_item WHERE cart_item_id = ?";
        try {
            jdbcTemplate.update(sql, cartItemId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public void clearCartByUserId(int userId) {
        String sql = "DELETE FROM cart_item WHERE user_id = ?";
        try {
            jdbcTemplate.update(sql, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public CartItem getCartItemById(int itemId) {
        CartItem cartItem = null;
        String sql = "SELECT ci.cart_item_id, ci.user_id, p.product_id, p.name AS product_name, ci.quantity, p.price, " +
                "(ci.quantity * p.price) AS total_price " +
                "FROM cart_item ci " +
                "JOIN product p ON ci.product_id = p.product_id " +
                "WHERE ci.cart_item_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, itemId);
            if (results.next()) {
                cartItem = mapRowToCartItem(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return cartItem;
    }

    @Override
    public int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, username);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    @Override
    public String getStateCodeByUserId(int userId) {
        String sql = "SELECT state_code FROM users WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    private CartItem mapRowToCartItem(SqlRowSet rowSet) {
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(rowSet.getInt("cart_item_id"));
        cartItem.setUserId(rowSet.getInt("user_id"));
        cartItem.setProductId(rowSet.getInt("product_id"));
        cartItem.setQuantity(rowSet.getInt("quantity"));
        cartItem.setProductName(rowSet.getString("product_name"));
        cartItem.setPrice(rowSet.getBigDecimal("price"));
        cartItem.setTotalPrice(rowSet.getBigDecimal("total_price"));
        return cartItem;
    }
}
