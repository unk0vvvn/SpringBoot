package com.portfolio.sb.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.portfolio.sb.domain.answer.dto.AnswerResponse;
import com.portfolio.sb.domain.comment.dto.CommentForm;
import com.portfolio.sb.domain.comment.dto.CommentResponse;
import com.portfolio.sb.exception.DataNotFoundException;
import com.portfolio.sb.service.AnswerService;
import com.portfolio.sb.service.CommentService;
import com.portfolio.sb.service.QuestionService;
import com.portfolio.sb.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comment")
public class CommentController {
	
	private final AnswerService answerService;
	private final QuestionService questionService;
	private final UserService userService;
	private final CommentService commentService;
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping(value={"/create/question/{id}", "/create/answer/{id}"})
	public String createQuestionComment(CommentForm commentForm) {
		return "comment_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/question/{id}")
	public String createQuestionComment(@PathVariable("id") Integer questionId
			,@Valid CommentForm commentForm, BindingResult bindingResult, Principal principal) {
		if(bindingResult.hasErrors()) {
			return "comment_form";
		}
		
		try {
			this.commentService.createComment(questionId, null, commentForm, principal.getName());
		} catch (DataNotFoundException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
		}
		
		return String.format("redirect:/question/detail/%s", questionId);
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/answer/{id}")
	public String createAnswerComment(@PathVariable("id") Integer answerId
			,@Valid CommentForm commentForm, BindingResult bindingResult, Principal principal) {
		if(bindingResult.hasErrors()) {
			return "comment_form";
		}
		
		AnswerResponse answerResponse = this.answerService.get(answerId);
		
		try {
			this.commentService.createComment(null, answerId, commentForm, principal.getName());
		} catch (DataNotFoundException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
		}
		
		return String.format("redirect:/question/detail/%s", answerResponse.getQuestionId());
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String modifyComment(CommentForm commentForm
			,@PathVariable("id") Integer commentId, Principal principal) {
		
		Optional<CommentResponse> optComment = this.commentService.get(commentId);
		if(optComment.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "comment not found");
		}
		
		CommentResponse commentResponse = optComment.get();
		if(!commentResponse.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		
		commentForm.setContent(commentResponse.getContent());
		
		return "comment_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String modifyComment(@Valid CommentForm commentForm, BindingResult bindingResult
			,@PathVariable("id") Long commentId, Principal principal) {
		if(bindingResult.hasErrors()) {
			return "comment_form";
		}
		
		Optional<CommentResponse> optComment = this.commentService.get(commentId);
		if(optComment.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "comment not found");
		}
		
		CommentResponse commentResponse = optComment.get();
		if(!commentResponse.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		
		commentResponse = this.commentService.modifyComment(commentResponse, commentForm);
		
		Long questionId = null;
		if(commentResponse.getQuestionId() != null)
			questionId = commentResponse.getQuestionId();
		else {
			AnswerResponse answerResponse = this.answerService.get(commentResponse.getAnswerId());
			questionId = answerResponse.getQuestionId();
		}
		
		return String.format("redirect:/question/detail/%s", questionId);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String deleteComment(@PathVariable("id") Integer commentId, Principal principal) {
		Optional<CommentResponse> optComment = this.commentService.get(commentId);
		if(optComment.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
		
		CommentResponse commentResponse = optComment.get();
		if(!principal.getName().equals(commentResponse.getAuthor().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
		}
		
		this.commentService.delete(commentResponse);
		
		Long questionId = null;
		if(commentResponse.getQuestionId() != null)
			questionId = commentResponse.getQuestionId();
		else {
			AnswerResponse answerResponse = this.answerService.get(commentResponse.getAnswerId());
			questionId = answerResponse.getQuestionId();
		}
		
		return String.format("redirect:/question/detail/%s", questionId);
	}
}
