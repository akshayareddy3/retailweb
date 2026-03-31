package com.food.foodorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.food.foodorder.model.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}