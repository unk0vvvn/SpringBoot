package com.portfolio.sb.domain.answer.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.portfolio.sb.domain.answer.Answer;
import com.portfolio.sb.domain.comment.dto.CommentResponse;
import com.portfolio.sb.domain.user.dto.UserResponse;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
public class AnswerResponse {
	private Long id;
	private Long questionId;
	private String content;
	
	private LocalDateTime createDate; 
	private LocalDateTime modifyDate;
	
	private UserResponse author;

	private Integer voterCount;
	
	private List<CommentResponse> comments;
	
	public static AnswerResponse fromEntity(Answer entity) {
		return AnswerResponse.builder()
				.id(entity.getId())
				.questionId(entity.getQuestion().getId())
				.content(entity.getContent())
				.createDate(entity.getCreateDate())
				.modifyDate(entity.getModifyDate())
				.author(UserResponse.fromEntity(entity.getAuthor()))
				.voterCount(entity.getVoters().size())
				.comments(CommentResponse.getCommentResponses(entity.getComments()))
				.build();
	}
	
	public static List<AnswerResponse> getAnswerRequests(List<Answer> answerList){
		return answerList.stream().map(AnswerResponse::fromEntity).toList();
	}
}
