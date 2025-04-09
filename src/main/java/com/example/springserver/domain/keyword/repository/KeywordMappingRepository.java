package com.example.springserver.domain.keyword.repository;

import com.example.springserver.entity.Customer;
import com.example.springserver.entity.Keyword;
import com.example.springserver.entity.KeywordMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordMappingRepository extends JpaRepository<KeywordMapping, Long> {
    List<KeywordMapping> findAllByCustomer(Customer customer);
}

