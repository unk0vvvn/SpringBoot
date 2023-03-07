package com.portfolio.sb.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.portfolio.sb.domain.answer.Answer;
import com.portfolio.sb.domain.answer.AnswerRepository;
import com.portfolio.sb.domain.comment.Comment;
import com.portfolio.sb.domain.comment.CommentRepository;
import com.portfolio.sb.domain.comment.dto.CommentForm;
import com.portfolio.sb.domain.comment.dto.CommentResponse;
import com.portfolio.sb.domain.question.Question;
import com.portfolio.sb.domain.question.QuestionRepository;
import com.portfolio.sb.domain.user.SiteUser;
import com.portfolio.sb.domain.user.UserRepository;
import com.portfolio.sb.exception.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	private final AnswerRepository answerRepository;
	private final CommentRepository commentRepository;
	private final QuestionRepository questionRepository;
	private final UserRepository userRepository;
	
	public CommentResponse createComment(Integer questionId, Integer answerId, CommentForm commentForm, String authorname) {
		Optional<SiteUser> optAuthor = userRepository.findByUsername(authorname);
		if(optAuthor.isEmpty())
			throw new DataNotFoundException("author not found");
		SiteUser author = optAuthor.get();
		
		Question question = null;
		if(questionId != null) {
			Optional<Question> optQuestion = questionRepository.findById(questionId);
			if (optQuestion.isEmpty())
				throw new DataNotFoundException("question not found");
			question = optQuestion.get();
		}
		
		Answer answer = null;
		if(answerId != null) {
			Optional<Answer> optAnswer = answerRepository.findById(answerId);
			if (optAnswer.isEmpty())
				throw new DataNotFoundException("answer not found");
			answer = optAnswer.get();
		}
		
		Comment comment = Comment.builder()
				.author(author)
				.content(commentForm.getContent())
				.question(question)
				.answer(answer)
				.build();
		commentRepository.save(comment);
		
		return CommentResponse.fromEntity(comment);
	}
	
	public Optional<CommentResponse> get(Integer id){
		return this.commentRepository.findById(id).map(CommentResponse::fromEntity);
	}
	
	public CommentResponse modifyComment(CommentResponse commentResponse, CommentForm commentForm) {
		
		Optional<SiteUser> optAuthor = userRepository.findByUsername(commentResponse.getAuthor().getUsername());
		if(optAuthor.isEmpty())
			throw new DataNotFoundException("author not found");
		SiteUser author = optAuthor.get();
		
		Question question = null;
		if(commentResponse.getQuestionId() != null) {
			Optional<Question> optQuestion = questionRepository.findById(commentResponse.getQuestionId());
			if (optQuestion.isEmpty())
				throw new DataNotFoundException("question not found");
			question = optQuestion.get();
		}
		
		Answer answer = null;
		if(commentResponse.getAnswerId() != null) {
			Optional<Answer> optAnswer = answerRepository.findById(commentResponse.getAnswerId());
			if (optAnswer.isEmpty())
				throw new DataNotFoundException("answer not found");
			answer = optAnswer.get();
		}
		
		commentResponse.setContent(commentForm.getContent());
		
		Comment comment = commentResponse.toEntity(question, answer, author);
		this.commentRepository.save(comment);
		
		return commentResponse;
	}
	
	public void delete(CommentResponse commentResponse) {
		this.commentRepository.deleteById(commentResponse.getId());
	}
}
