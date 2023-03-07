package com.portfolio.sb.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.portfolio.sb.domain.user.SiteUser;
import com.portfolio.sb.domain.user.UserRepository;
import com.portfolio.sb.domain.user.UserRole;
import com.portfolio.sb.domain.user.dto.UserResponse;
import com.portfolio.sb.exception.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public SiteUser create(String username, String password, String email) {
		UserRole role = null;
		if(username.equals("admin"))
			role = UserRole.ADMIN;
		else
			role = UserRole.USER;
		
		SiteUser user = SiteUser.builder()
				.username(username)
				.password(passwordEncoder.encode(password))
				.email(email)
				.role(role)
				.build();
		
		userRepository.save(user);
		
		return user;
	}
	
	public SiteUser getRegistrationByEmail(String email) {
		return this.userRepository.findByEmail(email).orElse(null);
	}
	
	public UserResponse getById(Integer id) {
		Optional<SiteUser> optUser = this.userRepository.findById(id);
		if(optUser.isPresent()) {
			return UserResponse.fromEntity(optUser.get());
		} else { 
			throw new DataNotFoundException("user not found.");
		}
	}
	
	public UserResponse getByUsername(String username) {
		Optional<SiteUser> optUser = this.userRepository.findByUsername(username);
		if(optUser.isPresent()) {
			return UserResponse.fromEntity(optUser.get());
		} else { 
			throw new DataNotFoundException(username + "user not found.");
		}
	}
	
	public UserResponse getByEmail(String email) {
		Optional<SiteUser> optUser = this.userRepository.findByEmail(email);
		if(optUser.isPresent()) {
			return UserResponse.fromEntity(optUser.get());
		} else { 
			throw new DataNotFoundException("user not found.");
		}
	}
	
	public Boolean isRightPassword(String username, String password) {
		
		String encodedPassword = this.userRepository.findPasswordByUsername(username);
		
		return this.passwordEncoder.matches(password, encodedPassword);
	}
	
	public void updatePassword(String username, String password) {
		
		this.userRepository.updatePassword(username, this.passwordEncoder.encode(password));
	}
	
	public void delete(String username) {
		this.userRepository.deleteByUsername(username);
	}
}
