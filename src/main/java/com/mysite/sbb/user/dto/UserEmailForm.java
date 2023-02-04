package com.mysite.sbb.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEmailForm {
	@Email(message = "올바른 이메일 주소를 입력해주세요.")
	@NotBlank(message = "이메일 주소를 입력해주세요.")
	String email;
}
