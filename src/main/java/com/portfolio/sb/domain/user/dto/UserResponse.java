package com.portfolio.sb.domain.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.portfolio.sb.domain.user.SiteUser;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
public class UserResponse {
	private Long id;
	private String username;
	private String email;
	
	public static UserResponse fromEntity(SiteUser entity) {
		if(entity == null) return null; // 테스트 데이터 때문
		
		return UserResponse.builder()
				.id(entity.getId())
				.username(entity.getUsername())
				.email(entity.getEmail())
				.build();
	}
	
	public static Set<UserResponse> getUserRequests(Set<SiteUser> userSet){
		return userSet.stream().map(UserResponse::fromEntity).collect(Collectors.toSet());
	}
}
