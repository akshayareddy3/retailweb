package com.food.foodorder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.food.foodorder.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}