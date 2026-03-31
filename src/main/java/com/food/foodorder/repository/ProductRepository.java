package com.food.foodorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.food.foodorder.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}