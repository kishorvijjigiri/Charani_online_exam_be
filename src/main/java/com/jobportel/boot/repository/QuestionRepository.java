package com.jobportel.boot.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Import this
import com.jobportel.boot.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Option A: If you are using a custom @Query
    @Query("SELECT q FROM Question q WHERE q.section = :section")
    List<Question> findBySectionIgnoreCase(@Param("section") String section);

    // Option B: If you are using Spring's method naming convention
    // Usually, @Param isn't needed here, but adding it fixes the IllegalStateException
    List<Question> findAllBySectionIgnoreCase(@Param("section") String section);
}