package com.techelevator.dao;

import com.techelevator.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductDao {
    List<Product> getAllProducts();
    List<Product> searchProductsBySku(String sku);
    List<Product> searchProductsByName(String name);
    List<Product> searchProductsBySkuAndName(String sku, String name);
    Product getProductById(int id);
}
