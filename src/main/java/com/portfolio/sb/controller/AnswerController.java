package com.portfolio.sb.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.portfolio.sb.domain.answer.dto.AnswerForm;
import com.portfolio.sb.domain.answer.dto.AnswerResponse;
import com.portfolio.sb.domain.question.dto.QuestionResponse;
import com.portfolio.sb.domain.user.dto.UserResponse;
import com.portfolio.sb.service.AnswerService;
import com.portfolio.sb.service.QuestionService;
import com.portfolio.sb.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {
	
	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UserService userService;
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String createAnswer(Model model, @PathVariable("id") Integer questionId
								,@Valid AnswerForm answerForm, BindingResult bindingResult
								, Principal principal) {
	
		QuestionResponse questionResponse = questionService.get(questionId);
		if(bindingResult.hasErrors()) {
			model.addAttribute("question", questionResponse);
			model.addAttribute("answerPaging", this.answerService.getListByQuestion(questionId, 0));
			return "question_detail";
		}
		
		AnswerResponse answerResponse = answerService.create(questionResponse, answerForm, principal.getName());
		
		return String.format("redirect:/question/detail/%s#answer_%s", questionId, answerResponse.getId());
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String modifyAnswer(AnswerForm answerForm, @PathVariable("id") Integer id
							,Principal principal) {
		AnswerResponse answerResponse = this.answerService.get(id);
		if(!principal.getName().equals(answerResponse.getAuthor().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		
		answerForm.setContent(answerResponse.getContent());
		
		return "answer_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String modifyAnswer(@Valid AnswerForm answerForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Principal principal) {
		if(bindingResult.hasErrors()) {
			return "answer_form";
		}
		
		AnswerResponse answerResponse = this.answerService.get(id);
		if(!principal.getName().equals(answerResponse.getAuthor().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		
		answerResponse = this.answerService.modify(answerResponse, answerForm);
		
		return String.format("redirect:/question/detail/%s#answer_%s", answerResponse.getQuestionId(), answerResponse.getId());
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String deleteAnswer(Principal principal, @PathVariable("id") Integer id) {
		AnswerResponse answerResponse = this.answerService.get(id);
		if(!principal.getName().equals(answerResponse.getAuthor().getUsername())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		this.answerService.delete(answerResponse);
		
		return "redirect:/question/detail/{id}";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String voteAnswer(Principal principal, @PathVariable("id") Integer id) {
		AnswerResponse answerResponse = this.answerService.get(id);
		UserResponse userResponse = this.userService.getByUsername(principal.getName());
		
		this.answerService.vote(answerResponse, userResponse);
		
		return String.format("redirect:/question/detail/%s#answer_%s", answerResponse.getQuestionId(), answerResponse.getId());
	}
}
