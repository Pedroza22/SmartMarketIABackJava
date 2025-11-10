package com.smartmarket.backend.service;

import com.smartmarket.backend.model.Product;
import com.smartmarket.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> list() {
        return productRepository.findAll();
    }

    public Product update(Long id, Product updated) {
        Product p = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        p.setName(updated.getName());
        p.setDescription(updated.getDescription());
        p.setPrice(updated.getPrice());
        return productRepository.save(p);
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        productRepository.deleteById(id);
    }
}