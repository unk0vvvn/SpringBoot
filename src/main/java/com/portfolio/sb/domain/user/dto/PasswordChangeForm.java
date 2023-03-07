package com.portfolio.sb.domain.user.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PasswordChangeForm {
	@NotBlank(message = "기존 비밀번호를 채워주세요.")
	private String prePassword;
	
	@NotBlank(message = "새 비밀번호를 채워주세요.")
	private String newPassword;
	
	@NotBlank(message = "비밀번호를 확인해주세요.")
	private String passwordConfirm;
}
