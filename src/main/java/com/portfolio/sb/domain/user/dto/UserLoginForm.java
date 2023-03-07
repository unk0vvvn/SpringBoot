package com.portfolio.sb.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginForm {
	private String username;
	private String password;
}
