package com.portfolio.sb.domain.question.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.portfolio.sb.domain.comment.dto.CommentResponse;
import com.portfolio.sb.domain.question.Question;
import com.portfolio.sb.domain.user.dto.UserResponse;

import java.util.LinkedList;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
public class QuestionResponse {
	private Long id;
	
	private String subject;
	private String content;
	
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	
	private UserResponse author;
	
	private Integer answerCount;
	
	private Integer voterCount;
	
	@Builder.Default
	private List<CommentResponse> comments = new LinkedList<CommentResponse>();
	
	@Builder.Default
	private Integer viewCount = 0;
	
	public static QuestionResponse fromEntity(Question entity) {
		return QuestionResponse.builder()
				.id(entity.getId())
				.subject(entity.getSubject())
				.content(entity.getContent())
				.createDate(entity.getCreateDate())
				.modifyDate(entity.getModifyDate())
				.author(UserResponse.fromEntity(entity.getAuthor()))
				.answerCount(entity.getAnswers().size())
				.voterCount(entity.getVoters().size())
				.comments(CommentResponse.getCommentResponses(entity.getComments()))
				.viewCount(entity.getViewCount())
				.build();
	}
}
