package com.mysite.sbb.answer;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
	
	@Query(value="SELECT distinct a.*, COUNT(av.answer_id) voter_count "
			+ "FROM ANSWER a "
			+ "LEFT OUTER JOIN ANSWER_VOTER_SET av ON av.answer_id = a.id "
			+ "WHERE a.question_id = :questionId "
			+ "GROUP BY a.id, answer_id "
			+ "ORDER BY voter_count DESC, create_date DESC"
			
			,countQuery = "SELECT COUNT(*) FROM ANSWER "
					+ "WHERE question_id = :questionId"
			,nativeQuery = true)
	Page<Answer> findAllByQuestionId(@Param("questionId")Integer QuesitonId, Pageable pageable);
}
