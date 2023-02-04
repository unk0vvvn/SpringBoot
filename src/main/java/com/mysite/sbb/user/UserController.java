package com.mysite.sbb.user;

import java.security.Principal;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.sbb.CommonUtil;
import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.MailService;
import com.mysite.sbb.user.dto.PasswordChangeForm;
import com.mysite.sbb.user.dto.UserCreateForm;
import com.mysite.sbb.user.dto.UserEmailForm;
import com.mysite.sbb.user.dto.UserResponse;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	
	private final CommonUtil commonUtil;
	private final UserService userService;
	private final MailService mailService;

	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}
	
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "signup_form";
		}
		
		if(!userCreateForm.getPassword().equals(userCreateForm.getPasswordConfirm())) {
			bindingResult.rejectValue("passwordConfirm", "passwordInCorrect"
					, "패스워드가 일치하지 않습니다." );
			return "signup_form";
		}
		
		try {
			userService.create(userCreateForm.getUsername(), userCreateForm.getPassword(), userCreateForm.getEmail());
			
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed"
					, "이미 등록된 사용자입니다.");
			return "signup_form";
			
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed"
					, e.getMessage());
			return "signup_form";
		}
		return "redirect:/user/login";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/withdraw")
	public String withdraw(Principal principal, HttpSession session) {
		this.userService.delete(principal.getName());
		
		return "redirect:/user/logout";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login_form";
	}
	
	@GetMapping("/forgotPassword")
	public String forgotPassword(UserEmailForm emailForm) {
		return "forgot_password_form";
	}
	
	@PostMapping("/forgotPassword")
	public String forgotPassword(@Valid UserEmailForm emailForm, BindingResult bindingResult
							,RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "forgot_password_form";
		}
		
		UserResponse userResponse = null;
		try {
			userResponse = this.userService.getByEmail(emailForm.getEmail());
		} catch (DataNotFoundException e) {
			bindingResult.rejectValue("email", "emailNotFound"
					, "해당 이메일의 사용자가 없습니다." );
			return "forgot_password_form";
		}
		
		String tempPassword = commonUtil.getRandomString(8);
		String subject = "포트폴리오(portfolio.kr) 비밀번호 변경 알림";
		String content = 
				"""
				<p>
				포트폴리오 <a href="http://portfolio.kr" target="_blank" rel="noopener noreferrer">(http://portfolio.kr)</a> 비밀번호가 초기화되었습니다.
				</p>
				<p>
				사용자 ID는 다음과 같습니다.
				<h2>
				"""
				+ userResponse.getUsername() +
				"""
				</h2>
				초기화된 비밀번호는 다음과 같습니다.
				<h2>
				"""
				+ tempPassword +
				"""
				</h2>
				비밀번호 보호를 위해 사이트에 로그인한 후 비밀번호를 다시 변경해 주세요.
				</p>
				""";
		
		try {
			mailService.sendMail(userResponse.getEmail(), subject, content);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		this.userService.updatePassword(userResponse.getUsername(), tempPassword);
		
		redirectAttributes.addFlashAttribute("success", "임시 비밀번호가 이메일로 전송되었습니다.");
		
		return "redirect:/user/forgotPassword";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/changePassword")
	public String changePassword(PasswordChangeForm passwordChangeForm) {
		return "password_change_form";
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/changePassword")
	public String changePassword(@Valid PasswordChangeForm passwordChangeForm, BindingResult bindingResult,
			Principal principal, RedirectAttributes redirectAttributes) {
		if(bindingResult.hasErrors()) {
			return "password_change_form";
		}
		
		if(!this.userService.isRightPassword(principal.getName(), passwordChangeForm.getPrePassword())) {
			bindingResult.rejectValue("prePassword", "passwordInCorrect"
					, "기존 비밀번호와 일치하지 않습니다." );
			return "password_change_form";
		}
		if(!passwordChangeForm.getNewPassword().equals(passwordChangeForm.getPasswordConfirm())){
			bindingResult.rejectValue("passwordConfirm", "passwordInCorrect"
					, "패스워드가 일치하지 않습니다." );
			return "password_change_form";
		}
		
		this.userService.updatePassword(principal.getName(), passwordChangeForm.getNewPassword());
		
		redirectAttributes.addFlashAttribute("success", "비밀번호가 변경되었습니다.");
		
		return "redirect:/user/changePassword";
	}
}
