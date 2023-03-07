package com.portfolio.sb.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.portfolio.sb.domain.question.Question;
import com.portfolio.sb.domain.question.QuestionRepository;
import com.portfolio.sb.domain.question.dto.QuestionForm;
import com.portfolio.sb.domain.question.dto.QuestionResponse;
import com.portfolio.sb.domain.user.SiteUser;
import com.portfolio.sb.domain.user.UserRepository;
import com.portfolio.sb.domain.user.dto.UserResponse;
import com.portfolio.sb.exception.DataNotFoundException;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class QuestionService {
	private final UserRepository userRepository;
	private final QuestionRepository questionRepository;
	
	public Page<QuestionResponse> getList(int page, String kw){
		
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		
		Page<Question> questions = this.questionRepository.findAllByKeyword(kw, pageable);
		
		return questions.map(QuestionResponse::fromEntity);
	}
	
	public QuestionResponse get(Integer id) {
		Optional<Question> optQuestion = questionRepository.findById(id);
		if(optQuestion.isPresent()) {
			return QuestionResponse.fromEntity(optQuestion.get());
		} else {
			throw new DataNotFoundException("question not found");
		}
	}
	
	public void create(QuestionForm questionForm, String username) {
		Optional<SiteUser> optUser = userRepository.findByUsername(username);
		if(optUser.isPresent()) {
			questionRepository.save(questionForm.toEntity(optUser.get()));
		} else {
			throw new DataNotFoundException("user not found");
		}
		
		
	}
	
	public void modify(Integer id, QuestionForm questionForm) {
		Optional<Question> optQuestion = this.questionRepository.findById(id);
		if(optQuestion.isPresent()) {
			Question preQuestion = optQuestion.get();
			
			Question modifiedQuestion = 
					Question.builder()
						.id(preQuestion.getId())
						.subject(questionForm.getSubject())
						.content(questionForm.getContent())
						.author(preQuestion.getAuthor())
						.answers(preQuestion.getAnswers())
						.createDate(preQuestion.getCreateDate())
						.voters(preQuestion.getVoters())
						.comments(preQuestion.getComments())
						.viewCount(preQuestion.getViewCount())
						.build();
			
			this.questionRepository.save(modifiedQuestion);
		} else {
			throw new DataNotFoundException("question not found");
		}
		
	}
	
	public void delete(Integer id) {
		this.questionRepository.deleteById(id);
	}
	
	public void deleteAll() {
		this.questionRepository.deleteAll();
	}
	
	public void vote(QuestionResponse questionResponse, UserResponse userResponse) {
		Optional<Question> optQuestion = this.questionRepository.findById(questionResponse.getId());
		if(optQuestion.isEmpty()) {
			throw new DataNotFoundException("question not found");
		}
		
		Optional<SiteUser> optUser = this.userRepository.findByUsername(userResponse.getUsername());
		if(optUser.isEmpty()) {
			throw new DataNotFoundException("user not found");
		}
		
		Question question = optQuestion.get();
		question.getVoters().add(optUser.get());
		this.questionRepository.save(question);
	}
	
	@Transactional
	public void updateViewCount(g id, HttpServletRequest request, HttpServletResponse response) {
		Cookie oldCookie = null;
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie: cookies) {
				if(cookie.getName().equals("questionView")) {
					oldCookie = cookie;
					break;
				}
			}
		}
		
		if(oldCookie == null) {
			Cookie newCookie = new Cookie("questionView", "[" + id + "]");
			newCookie.setMaxAge(60 * 60 * 24);
			newCookie.setPath("/question");
			response.addCookie(newCookie);
			
			this.questionRepository.updateViewCount(id);
			
		} else if(!oldCookie.getValue().contains("[" + id + "]")){
			oldCookie.setValue(oldCookie.getValue() + "[" + id + "]");
			oldCookie.setMaxAge(60 * 60 * 24);
			response.addCookie(oldCookie);
			
			this.questionRepository.updateViewCount(id);
		}
		
		
	}
}
