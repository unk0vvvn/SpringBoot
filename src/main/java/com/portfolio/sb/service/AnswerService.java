package com.portfolio.sb.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.portfolio.sb.domain.answer.Answer;
import com.portfolio.sb.domain.answer.AnswerRepository;
import com.portfolio.sb.domain.answer.dto.AnswerForm;
import com.portfolio.sb.domain.answer.dto.AnswerResponse;
import com.portfolio.sb.domain.question.Question;
import com.portfolio.sb.domain.question.QuestionRepository;
import com.portfolio.sb.domain.question.dto.QuestionResponse;
import com.portfolio.sb.domain.user.SiteUser;
import com.portfolio.sb.domain.user.UserRepository;
import com.portfolio.sb.domain.user.dto.UserResponse;
import com.portfolio.sb.exception.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {

	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;
	private final UserRepository userRepository;
	
	public AnswerResponse create(QuestionResponse questionResponse, AnswerForm answerForm, String username) {
		Optional<Question> optQuestion = questionRepository.findById(questionResponse.getId());
		Question question;
		if(optQuestion.isPresent()) {
			question = optQuestion.get();
		} else {
			throw new DataNotFoundException("question not found");
		}
		
		Optional<SiteUser> optUser = userRepository.findByUsername(username);
		SiteUser author;
		if(optUser.isPresent()) {
			author = optUser.get();
		} else {
			throw new DataNotFoundException("author not found");
		}
		
		Answer answer = Answer.builder()
				.question(question)
				.content(answerForm.getContent())
				.author(author)
				.build();
		
		answerRepository.save(answer);
		
		return AnswerResponse.fromEntity(answer);
	}
	
	public AnswerResponse get(int id) {
		Optional<Answer> optAnswer = this.answerRepository.findById(id);
		if(optAnswer.isPresent()) {
			return AnswerResponse.fromEntity(optAnswer.get());
		} else {
			throw new DataNotFoundException("answer not found.");
		}
	}
	
	public Page<AnswerResponse> getListByQuestion(int questionId, int page){
		List<Sort.Order> sorts = new ArrayList<>();
		Pageable pageable = PageRequest.of(page, 3, Sort.by(sorts));
		
		Page<Answer> paging = this.answerRepository.findAllByQuestionId(questionId, pageable);
		
		return paging.map(AnswerResponse::fromEntity);
	}
	
	public AnswerResponse modify(AnswerResponse answerResponse, AnswerForm answerForm) {
		Optional<Answer> optAnswer = answerRepository.findById(answerResponse.getId());
		if(optAnswer.isPresent()) {
			Answer answer = optAnswer.get();
			
			Answer modifiedAnswer = 
					Answer.builder()
						.id(answer.getId())
						.author(answer.getAuthor())
						.content(answerForm.getContent())
						.question(answer.getQuestion())
						.voters(answer.getVoters())
						.comments(answer.getComments())
						.build();
			
			this.answerRepository.save(modifiedAnswer);
			
			return AnswerResponse.fromEntity(modifiedAnswer);
		} else {
			throw new DataNotFoundException("answer not found");
		}
	}
	
	public void delete(AnswerResponse answerResponse) {
		this.answerRepository.deleteById(answerResponse.getId());
	}
	
	public void vote(AnswerResponse answerResponse, UserResponse userResponse) {
		Answer answer = this.answerRepository.getReferenceById(answerResponse.getId());
		
		Optional<SiteUser> optUser = this.userRepository.findByUsername(userResponse.getUsername());
		if(optUser.isEmpty())
			throw new DataNotFoundException("user not found");
		
		answer.getVoters().add(optUser.get());
		this.answerRepository.save(answer);
	}
}
