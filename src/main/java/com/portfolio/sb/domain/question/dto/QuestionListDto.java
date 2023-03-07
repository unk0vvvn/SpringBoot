package com.portfolio.sb.domain.question.dto;

import java.time.LocalDateTime;
import java.util.List;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
public class QuestionListDto {
	private Integer id;
	
	private String subject;
	
	private String author;
	
	private LocalDateTime createdDate;
	
	private Integer viewCount;
	private Integer answerCount;
	
//	public static QuestionListDto fromEntity(Question q) {
//		return QuestionListDto.builder()
//				.id(q.getId())
//				.
//	}
}
