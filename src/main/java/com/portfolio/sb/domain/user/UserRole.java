package com.portfolio.sb.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
	ADMIN("ROLE_ADMIN"),
	USER("ROLE_USER");
	
	private String value;
	
	UserRole(String _value){
		value = _value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
