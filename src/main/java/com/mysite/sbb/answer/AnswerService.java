package com.mysite.sbb.answer;

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

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.dto.AnswerForm;
import com.mysite.sbb.answer.dto.AnswerResponse;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.dto.QuestionResponse;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.dto.UserResponse;

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
				.createDate(LocalDateTime.now())
				.voterSet(new HashSet<>())
				.commentList(new LinkedList<>())
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
						.createDate(answer.getCreateDate())
						.modifyDate(LocalDateTime.now())
						.question(answer.getQuestion())
						.voterSet(answer.getVoterSet())
						.commentList(answer.getCommentList())
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
		
		answer.getVoterSet().add(optUser.get());
		this.answerRepository.save(answer);
	}
}
