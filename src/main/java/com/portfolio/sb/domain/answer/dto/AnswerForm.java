package com.portfolio.sb.domain.answer.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AnswerForm {
	@NotEmpty(message="내용을 채워주세요.")
	private String content;
}
