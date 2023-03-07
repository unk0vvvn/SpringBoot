package com.portfolio.sb.domain.user.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserEmailForm {
	@Email(message = "올바른 이메일 주소를 입력해주세요.")
	@NotBlank(message = "이메일 주소를 입력해주세요.")
	String email;
}
