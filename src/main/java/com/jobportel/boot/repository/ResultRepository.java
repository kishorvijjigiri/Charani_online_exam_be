package com.jobportel.boot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jobportel.boot.model.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    
    Optional<Result> findByCandidateEmail(String candidateEmail);

    // Filter by email (partial match) AND percentage (greater than or equal to)
    @Query("SELECT r FROM Result r WHERE " +
    	       "(:email IS NULL OR r.candidateEmail LIKE %:email%) AND " +
    	       "(:minPct IS NULL OR r.percentage >= :minPct) " +
    	       "ORDER BY r.percentage DESC")
    	List<Result> findResultsWithFilters(@Param("email") String email, @Param("minPct") Double minPct);
    }

