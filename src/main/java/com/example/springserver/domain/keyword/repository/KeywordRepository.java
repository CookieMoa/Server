package com.example.springserver.domain.keyword.repository;

import com.example.springserver.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    List<Keyword> findAllByNameIn(List<String> names);
}

