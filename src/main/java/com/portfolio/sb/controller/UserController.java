package com.portfolio.sb.controller;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.sb.domain.user.SiteUser;
import com.portfolio.sb.domain.user.dto.PasswordChangeForm;
import com.portfolio.sb.domain.user.dto.UserCreateForm;
import com.portfolio.sb.domain.user.dto.UserEmailForm;
import com.portfolio.sb.domain.user.dto.UserResponse;
import com.portfolio.sb.exception.DataNotFoundException;
import com.portfolio.sb.security.auth.oauth.KakaoProfile;
import com.portfolio.sb.security.auth.oauth.OAuthService;
import com.portfolio.sb.security.auth.oauth.OAuthToken;
import com.portfolio.sb.service.MailService;
import com.portfolio.sb.service.SecurityService;
import com.portfolio.sb.service.UserService;
import com.portfolio.sb.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {
	@Value("${auth.key}")
	private String oauthKey;
	
	private final CommonUtil commonUtil;
	private final UserService userService;
	private final MailService mailService;
	private final OAuthService oauthService;
	private final SecurityService securityService;
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/adminpage")
	public @ResponseBody String adminpage(){
		return "redirect:/";
	}

	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}
	
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, HttpServletResponse response) {
		if(bindingResult.hasErrors()) {
			return "signup_form";
		}
		
		if(!userCreateForm.getPassword().equals(userCreateForm.getPasswordConfirm())) {
			bindingResult.rejectValue("passwordConfirm", "passwordInCorrect"
					, "패스워드가 일치하지 않습니다." );
			return "signup_form";
		}
		
		SiteUser user = null;
		try {
			user = userService.create(userCreateForm.getUsername(), userCreateForm.getPassword(), userCreateForm.getEmail());
			
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
		
		this.securityService.doAutoLogin(userCreateForm.getUsername(), userCreateForm.getPassword(), response);
		
		return "redirect:/";
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
	
	@GetMapping("/login/oauth2/code/kakao")
	public String kakaoCode(String code, HttpServletResponse response) {
		
		OAuthToken token = oauthService.getOAuthToken(code);
		KakaoProfile profile = oauthService.getKakaoProfile(token);
		
		String username = "KAKAO_" + profile.getId();
		String email = profile.getKakao_account().getEmail();
		
		SiteUser user = this.userService.getRegistrationByEmail(email);
		if(user == null) {
			user = this.userService.create(username, this.oauthKey, email);
		}
		
		securityService.doAutoLogin(user.getUsername(), this.oauthKey, response);
		return "redirect:/"; 
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
