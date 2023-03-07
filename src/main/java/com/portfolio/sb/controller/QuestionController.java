package com.portfolio.sb.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.portfolio.sb.domain.answer.dto.AnswerForm;
import com.portfolio.sb.domain.question.dto.QuestionForm;
import com.portfolio.sb.domain.question.dto.QuestionResponse;
import com.portfolio.sb.domain.user.dto.UserResponse;
import com.portfolio.sb.service.AnswerService;
import com.portfolio.sb.service.QuestionService;
import com.portfolio.sb.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/question")
public class QuestionController {
	
	private final AnswerService answerService;
	private final UserService userService;
	private final QuestionService questionService;
	
	@GetMapping("/list")
	public String getList(Model model
			,@RequestParam(value="page", defaultValue="0") int page
			,@RequestParam(value="kw", defaultValue="") String kw) {
		
		Page<QuestionResponse> paging = questionService.getList(page, kw);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
		
		return "question_list";
	}
	
	@GetMapping("/detail/{id}")
	public String getDetail(Model model, @PathVariable("id") Integer questionId
							, AnswerForm answerForm, @RequestParam(value="page", defaultValue="0") int page
							, HttpServletRequest request, HttpServletResponse response) {
		QuestionResponse questionResponse = questionService.get(questionId);
		
		model.addAttribute("question", questionResponse);
		model.addAttribute("answerPaging", this.answerService.getListByQuestion(questionId, page));
		
		
		
		this.questionService.updateViewCount(questionId, request, response);
		
		return "question_detail";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/create")
	public String createQuestion(QuestionForm questionForm) {
		return "question_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String createQuestion(@Valid QuestionForm questionForm,BindingResult bindingResult 
			, Principal principal) {
		if(bindingResult.hasErrors()) {
			return "question_form";
		}
		
		questionService.create(questionForm, principal.getName());
		
		return "redirect:/question/list";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String modifyQuestion(QuestionForm questionForm, @PathVariable("id") Integer id
			, Principal principal) {
		QuestionResponse questionResponse = questionService.get(id);
		if(!questionResponse.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		
		questionForm.setSubject(questionResponse.getSubject());
		questionForm.setContent(questionResponse.getContent());
		
		return "question_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String modifyQuestion(@Valid QuestionForm questionForm, BindingResult bindingResult,
			@PathVariable("id") Integer id, Principal principal) {
		if(bindingResult.hasErrors()) {
			return "question_form";
		}
		
		QuestionResponse questionResponse = this.questionService.get(id);
		if(!questionResponse.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		
		this.questionService.modify(id, questionForm);
		
		return "redirect:/question/detail/{id}";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String deleteQuestion(Principal principal, @PathVariable("id") Integer id) {
		QuestionResponse questionResponse = this.questionService.get(id);
		if(!questionResponse.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
		}
		
		this.questionService.delete(questionResponse.getId());
		return "redirect:/";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String questionVote(Principal principal, @PathVariable("id") Integer id) {
		QuestionResponse questionResponse = this.questionService.get(id);
		UserResponse userResponse = this.userService.getByUsername(principal.getName());
		
		this.questionService.vote(questionResponse, userResponse);
		
		return "redirect:/question/detail/{id}";
	}
	
	
	
	
	
	
	
	
	
	
}
