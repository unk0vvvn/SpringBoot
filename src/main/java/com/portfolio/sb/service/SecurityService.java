package com.portfolio.sb.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SecurityService {
	public void doAutoLogin(String username, String password, HttpServletResponse httpResponse) {
		
		HttpHeaders headers = new HttpHeaders();
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY
				, username);
		params.add(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY
				, password);
		
		HttpEntity<MultiValueMap<String,String>> CookieRequest =
				new HttpEntity<>(params, headers);
		
		RestTemplate rt = new RestTemplate();
		
		ResponseEntity<String> response = rt.exchange(
				"http://localhost:8080/login",
				HttpMethod.POST,
				CookieRequest,
				String.class
		);
		String setCookie = response.getHeaders().getFirst("Set-Cookie");
		
		httpResponse.setHeader("Set-Cookie", setCookie);
	};
}
