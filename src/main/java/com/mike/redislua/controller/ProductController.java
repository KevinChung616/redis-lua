package com.mike.redislua.controller;

import com.mike.redislua.domain.Product;
import com.mike.redislua.exception.SoldOutException;
import com.mike.redislua.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product product) {
        return ResponseEntity.ok(productService.save(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable int id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }

    @GetMapping("")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }
    
    @PostMapping("/decrement/{id}")
    public ResponseEntity<Product> decrementQtyById(@PathVariable int id) {
        return ResponseEntity.ok(productService.decrementQtyById(id));
    }

    @PostMapping("/decrement/lua/{id}")
    public ResponseEntity<Product> decrementQtyByIdLua(@PathVariable int id) throws SoldOutException {
        return ResponseEntity.ok(productService.decrementQtyByIdLua(id));
    }
}
