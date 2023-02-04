package com.mysite.sbb.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
	@NotEmpty(message = "내용을 채워주세요.")
	private String content;
}
