package com.example.mywebsite.service;

import com.example.mywebsite.entity.Product;
import com.example.mywebsite.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return (List<Product>) productRepository.findAll();
    }

    public Product getProductByName(String name) {
        return productRepository.getFirstByName(name);
    }

    public Product getProductById(Long id) {
        return productRepository.getProductById(id);
    }

    public boolean exists(Long id) {
        return productRepository.existsById(id);
    }

    public String createProduct(String name, String description, Double price, byte[] image, boolean configurable, List<String> variants) throws IOException {

        Product product = new Product();

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setConfigurable(configurable);
        product.setVariants(variants != null ? variants : new ArrayList<>());

        if (image == null) {
            product.setImage(Files.readAllBytes(Path.of("src/main/resources/static/images/products/john_kler.png")));
        } else {
            product.setImage(image);
        }

        try {
            productRepository.save(product);
            return "successful";
        } catch (Exception e) {
            return "error: " + e;
        }
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
