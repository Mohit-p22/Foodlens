package com.example.foodlens.repositories;

import com.example.foodlens.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    Optional<Product> findByCategoryAndName(String category, String name);
    Optional<Product> findByName(String name);
}