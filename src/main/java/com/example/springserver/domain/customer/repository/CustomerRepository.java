package com.example.springserver.domain.customer.repository;

import com.example.springserver.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserId(Long userId);

    Page<Customer> findByNameStartingWith(String query, Pageable pageable);
}

