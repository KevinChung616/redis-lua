package com.mike.redislua.service;

import com.mike.redislua.domain.Product;
import com.mike.redislua.exception.SoldOutException;
import com.mike.redislua.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findProductById(int id) {
        return productRepository.findProductById(id);
    }

    public String deleteProduct(int id) {
        return productRepository.deleteProduct(id);
    }

    public Product decrementQtyById(int id) {
        return productRepository.decrementQtyById(id);
    }

    public Product decrementQtyByIdLua(int id) throws SoldOutException {
        return productRepository.decrementQtyByIdLua(id);
    }
}
