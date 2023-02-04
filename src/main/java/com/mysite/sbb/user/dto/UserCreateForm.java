package com.mysite.sbb.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {

	@Size(min=3, max=25, message="ID길이는 3~25 범위여야 합니다.")
	@NotBlank(message = "사용자ID는 필수항목입니다.")
	private String username;
	
	@NotBlank(message = "비밀번호는 필수항목입니다.")
	private String password;
	
	@NotBlank(message = "비밀번호 확인은 필수항목입니다.")
	private String passwordConfirm;
	
	@Email
	@NotBlank(message = "email은 필수항목입니다.")
	private String email;
}
