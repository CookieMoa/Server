package com.example.springserver.domain.cafe.service;

import com.example.springserver.domain.cafe.repository.ReviewRepository;
import com.example.springserver.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review toReview(Review review) {
        return reviewRepository.save(review);
    }

    public Page<Review> findReviewByCafeId(Long cafeId, Pageable pageable) { return reviewRepository.findByCafeIdOrderByCreatedAtDesc(cafeId, pageable);}
    public Page<Review> findReviewByCustomerId(Long customerId, Pageable pageable) { return reviewRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable);}

    public Page<Review> findReviewByCustomerId(Long customerId, Boolean isMalicious, Pageable pageable) { return reviewRepository.findByCustomerIdAndIsMalicious(customerId, isMalicious,pageable);}

    public Page<Review> findReviewByIsMalicious(Boolean isMalicious, Pageable pageable) { return reviewRepository.findAllByIsMaliciousOrderByCreatedAtDesc(isMalicious,pageable);}

    public List<Review> findAll(){
        return reviewRepository.findAllBy();
    }

    public Integer maliciousReviewCount(Long customerId){
        return reviewRepository.countAllByCustomerIdAndIsMalicious(customerId, true);
    }
}
