package com.mysite.sbb.question.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class QuestionForm {
	
	@NotEmpty(message="제목은 필수항목입니다.")
	@Size(max = 200)
	private String subject;
	
	@NotEmpty(message="내용은 필수항목입니다.")
	private String content;
	
	public Question toEntity(SiteUser author) {
		return Question.builder()
				.subject(subject)
				.content(content)
				.createDate(LocalDateTime.now())
				.author(author)
				.build();
	}
}
