package com.example.springserver.domain.keyword.repository;

import com.example.springserver.entity.Cafe;
import com.example.springserver.entity.Customer;
import com.example.springserver.entity.KeywordMapping;
import com.example.springserver.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordMappingRepository extends JpaRepository<KeywordMapping, Long> {
    List<KeywordMapping> findAllByCustomer(Customer customer);
    List<KeywordMapping> findAllByCafe(Cafe cafe);
    List<KeywordMapping> findAllByReview(Review review);
    void deleteByCustomer(Customer customer);
}

