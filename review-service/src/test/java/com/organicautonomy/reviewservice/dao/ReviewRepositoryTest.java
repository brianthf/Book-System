package com.organicautonomy.reviewservice.dao;

import com.organicautonomy.reviewservice.dto.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository repository;
    private Review review1, review2;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        review1 = new Review(1, 1, new BigDecimal("3.10"), "It was decent. Not necessarily my fav tho.");
        review2 = new Review(2, 2, new BigDecimal("4.10"), "Great book.");
    }

    @Test
    void testSaveFindReview() {
        review1 = repository.save(review1);
        Optional<Review> fromRepository = repository.findById(review1.getId());

        assertEquals(review1, fromRepository.get());
    }

    @Test
    void testFindReviewsByUserId() {
        review1 = repository.save(review1);
        List<Review> reviews = repository.findReviewsByUserId(review1.getUserId());

        assertEquals(1, reviews.size());
    }

    @Test
    void testFindReviewsByBookId() {
        review1 = repository.save(review1);
        List<Review> reviews = repository.findReviewsByBookId(review1.getBookId());

        assertEquals(1, reviews.size());
    }


    @Test
    void testFindReviewsByRating() {
        review1 = repository.save(review1);
        review2 = repository.save(review2);

        List<Review> reviews = repository.findReviewsByRating(3);

        assertEquals(2, reviews.size());
    }
}