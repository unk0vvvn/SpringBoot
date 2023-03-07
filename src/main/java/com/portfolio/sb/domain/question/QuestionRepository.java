package com.portfolio.sb.domain.question;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.QueryHint;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
	
	@EntityGraph(attributePaths = {"author", "voters"})
	Optional<Question> findById(Long id);
	
	@EntityGraph(attributePaths = {"author", "voters"})
	@Query("SELECT "
			+ "distinct q "
			+ "from Question q "
			+ "WHERE "
			+ "	q.subject like %:kw%"
			+ "	or q.content like %:kw%"
			)
	Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
	
	@Modifying
	@Query("UPDATE Question SET viewCount = viewCount+1 WHERE id = :id")
	Integer updateViewCount(@Param("id") Long id);
}
