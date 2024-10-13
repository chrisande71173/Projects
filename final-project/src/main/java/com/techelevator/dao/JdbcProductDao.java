package com.techelevator.dao;
import com.techelevator.model.Product;
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
public class JdbcProductDao implements ProductDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcProductDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Product> getAllProducts() {
        String sql = "SELECT * FROM product";
        List<Product> products = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                products.add(mapRowToProduct(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new RuntimeException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Data integrity violation", e);
        }
        return products;
    }

    @Override
    public List<Product> searchProductsBySku(String sku) {
        String sql = "SELECT * FROM product WHERE product_sku = ?";
        List<Product> products = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, sku);
            while (results.next()) {
                products.add(mapRowToProduct(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new RuntimeException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Data integrity violation", e);
        }
        return products;
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        String sql = "SELECT * FROM product WHERE name ILIKE ?";
        List<Product> products = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, "%" + name + "%");
            while (results.next()) {
                products.add(mapRowToProduct(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new RuntimeException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Data integrity violation", e);
        }
        return products;
    }

    @Override
    public List<Product> searchProductsBySkuAndName(String sku, String name) {
        String sql = "SELECT * FROM product WHERE product_sku = ? AND name ILIKE ?";
        List<Product> products = new ArrayList<>();
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, sku, "%" + name + "%");
            while (results.next()) {
                products.add(mapRowToProduct(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new RuntimeException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Data integrity violation", e);
        }
        return products;
    }

    @Override
    public Product getProductById(int id) {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                return mapRowToProduct(results);
            } else {
                return null;
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new RuntimeException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Data integrity violation", e);
        }
    }
    private Product mapRowToProduct(SqlRowSet rowSet) {
        Product product = new Product();
        product.setProductId(rowSet.getInt("product_id"));
        product.setProductSku(rowSet.getString("product_sku"));
        product.setName(rowSet.getString("name"));
        product.setDescription(rowSet.getString("description"));
        product.setPrice(rowSet.getBigDecimal("price"));
        product.setImageName(rowSet.getString("image_name"));
        return product;
    }

}
