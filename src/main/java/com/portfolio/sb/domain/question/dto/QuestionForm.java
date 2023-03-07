package com.portfolio.sb.domain.question.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.portfolio.sb.domain.question.Question;
import com.portfolio.sb.domain.user.SiteUser;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
public class QuestionForm {
	
	@NotEmpty(message="제목은 필수항목입니다.")
	@Size(max = 200)
	private String subject;
	
	@NotEmpty(message="내용은 필수항목입니다.")
	private String content;
	
//	public Question toEntity(SiteUser author) {
//		return Question.builder()
//				.subject(subject)
//				.content(content)
//				.author(author)
//				.build();
//	}
}
