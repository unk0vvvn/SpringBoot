package com.mysite.sbb.user.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.mysite.sbb.user.SiteUser;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter	
public class UserResponse {
	private Integer id;
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
