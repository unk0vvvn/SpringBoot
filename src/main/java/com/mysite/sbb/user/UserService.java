package com.mysite.sbb.user;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public void create(String username, String password, String email) {
		SiteUser user = SiteUser.builder()
				.username(username)
				.password(passwordEncoder.encode(password))
				.email(email)
				.build();
		
		userRepository.save(user);
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
			throw new DataNotFoundException("user not found.");
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
