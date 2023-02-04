package com.mysite.sbb.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeForm {
	@NotBlank(message = "기존 비밀번호를 채워주세요.")
	private String prePassword;
	
	@NotBlank(message = "새 비밀번호를 채워주세요.")
	private String newPassword;
	
	@NotBlank(message = "비밀번호를 확인해주세요.")
	private String passwordConfirm;
}
