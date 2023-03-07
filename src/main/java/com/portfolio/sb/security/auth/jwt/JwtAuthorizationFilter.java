package com.portfolio.sb.security.auth.jwt;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.portfolio.sb.domain.user.SiteUser;
import com.portfolio.sb.domain.user.UserRepository;
import com.portfolio.sb.security.auth.PrincipalDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
	private UserRepository userRepository;
	
	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		Cookie[] cookies = request.getCookies();
		
		String jwtValue = null;
		if(cookies != null) {
			for (var c : cookies) {
				if (c.getName().equals(JwtProperties.HEADER_STRING)) {
					jwtValue = c.getValue();
					break;
				}
			}
		}
		if(jwtValue == null || !jwtValue.startsWith(JwtProperties.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
			return;
		}
		
		jwtValue = URLDecoder.decode(jwtValue, JwtProperties.ENCODE);
		String jwt = jwtValue.split(" ")[1];
		
		try {
			String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwt)
					.getClaim("username").asString();

			if (username != null) {
				Optional<SiteUser> optUser = userRepository.findByUsername(username);
				if (optUser.isEmpty())
					throw new JWTVerificationException(username + "NOT FOUND");

				SiteUser user = optUser.get();

				PrincipalDetails principalDetails = new PrincipalDetails(user);

				Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null,
						principalDetails.getAuthorities());

				SecurityContext context = SecurityContextHolder.getContext();
				context.setAuthentication(authentication);
				
			}
		} catch(JWTVerificationException e) {
			log.warn(e.getMessage());
		} 
		
		
		chain.doFilter(request, response);
	}
}
