package com.lankaconnect.ccms.repository;

import com.lankaconnect.ccms.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByTicketId(Long ticketId);
    List<Feedback> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Feedback> findByNegativeFollowUpRequiredTrueOrderByCreatedAtDesc();
    List<Feedback> findAllByOrderByCreatedAtDesc();

    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double getAverageRating();

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.rating >= 4")
    long countPositiveRatings();

    @Query("SELECT COUNT(f) FROM Feedback f")
    long countTotalFeedback();

    @Query("SELECT f.rating, COUNT(f) FROM Feedback f GROUP BY f.rating ORDER BY f.rating DESC")
    List<Object[]> countFeedbackByRating();
}
