package com.portfolio.sb.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.portfolio.sb.domain.user.UserRepository;
import com.portfolio.sb.domain.user.UserRole;
import com.portfolio.sb.security.auth.jwt.JwtAuthenticationFilter;
import com.portfolio.sb.security.auth.jwt.JwtAuthorizationFilter;
import com.portfolio.sb.security.auth.jwt.JwtProperties;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled=true, securedEnabled=true)
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final UserRepository userRepository;
	
	public class CustomSecurityFilterManager 
				extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		
			http.addFilter(new JwtAuthenticationFilter(authenticationManager))
				.addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
		}
	}
	
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.csrf().disable();
		
		http
			.cors().configurationSource(corsConfigurationSource())
		.and()
			.httpBasic().disable()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			
			.and()
				.apply(new CustomSecurityFilterManager())
					
			.and()
				.formLogin().disable()
				
				.exceptionHandling()
		        .authenticationEntryPoint(new AuthenticationEntryPoint() {        // 인증 실패 시 처리
		            @Override
		            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
		            	response.sendRedirect("/user/login");
		            	log.info("authentication fail");
		            }
		        })
		        .accessDeniedHandler(new AccessDeniedHandler() {                // 인가 실패 시 처리
					@Override
					public void handle(HttpServletRequest request, HttpServletResponse response,
							AccessDeniedException e) throws IOException, ServletException {
						System.out.println(request.getUserPrincipal());
						response.sendRedirect("/question/list");
						log.info("AccessDenied");
					}
		        })
		        
		    .and()
				.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
				.logoutSuccessUrl("/")
				.deleteCookies(JwtProperties.HEADER_STRING)
			.and()
				.authorizeHttpRequests()
				.requestMatchers("/userpage/**").authenticated()
				.requestMatchers("/adminpage/**").hasAuthority(UserRole.ADMIN.getValue())
				.anyRequest().permitAll()
			
			.and()
				.headers()
				.addHeaderWriter(new XFrameOptionsHeaderWriter(
							XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
						))
			
			;
		
	        
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationManager authenticationManager 
	(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		return source;
	}
	
}
