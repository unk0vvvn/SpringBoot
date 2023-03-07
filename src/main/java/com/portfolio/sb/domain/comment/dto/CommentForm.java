package com.portfolio.sb.domain.comment.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class CommentForm {
	@NotEmpty(message = "내용을 채워주세요.")
	private String content;
}
