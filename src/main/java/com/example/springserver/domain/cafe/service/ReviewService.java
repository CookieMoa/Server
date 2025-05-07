package com.example.springserver.domain.cafe.service;

import com.example.springserver.domain.cafe.repository.ReviewRepository;
import com.example.springserver.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review toReview(Review review) {
        return reviewRepository.save(review);
    }

    public Page<Review> findReviewByCafeId(Long cafeId, Pageable pageable) { return reviewRepository.findByCafeId(cafeId, pageable);}
}
