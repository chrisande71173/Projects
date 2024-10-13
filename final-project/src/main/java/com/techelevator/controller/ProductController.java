package com.techelevator.controller;

import com.techelevator.dao.ProductDao;
import com.techelevator.model.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductDao productDao;

    public ProductController(ProductDao productDao) {
        this.productDao = productDao;
    }
    @GetMapping
    public List<Product> searchProducts(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) String name) {
        if (sku != null && name != null) {
            return productDao.searchProductsBySkuAndName(sku, name);
        } else if (sku != null) {
            return productDao.searchProductsBySku(sku);
        } else if (name != null) {
            return productDao.searchProductsByName(name);
        } else {
            return productDao.getAllProducts();
        }
    }

    // Get a single product by ID
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable int id) {
        return productDao.getProductById(id);
    }
}
