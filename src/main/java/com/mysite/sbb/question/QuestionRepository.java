package com.mysite.sbb.question;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
	Question findBySubject(String subject);
	Question findBySubjectAndContent(String subject, String Content);
	List<Question> findBySubjectLike(String subject);
	List<Question> findByIdBetween(int from, int to);
	Page<Question> findAll(Pageable pageable);
	Page<Question> findAll(Specification<Question> spec, Pageable pageable);
	
	@Query("SELECT "
			+ "distinct q "
			+ "from Question q "
			+ "left outer join SiteUser u1 on q.author = u1 "
			+ "left outer join Answer a on a.question = q "
			+ "left outer join SiteUser u2 on u2 = a.author "
			+ "WHERE "
			+ "	q.subject like %:kw%"
			+ "	or q.content like %:kw%"
			+ " or u1.username like %:kw%"
			+ "	or a.content like %:kw%"
			+ " or u2.username like %:kw%"
			)
	Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
	
	@Modifying
	@Query("UPDATE Question SET viewCount = viewCount+1 WHERE id = :id")
	Integer updateViewCount(@Param("id") Integer id);
}
