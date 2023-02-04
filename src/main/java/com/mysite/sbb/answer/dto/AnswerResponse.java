package com.mysite.sbb.answer.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.comment.dto.CommentResponse;
import com.mysite.sbb.user.dto.UserResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AnswerResponse {
	private Integer id;
	private Integer questionId;
	private String content;
	
	private LocalDateTime createDate; 
	private LocalDateTime modifyDate;
	
	private UserResponse author;

	private Integer voterCount;
	
	private List<CommentResponse> commentList;
	
	public static AnswerResponse fromEntity(Answer entity) {
		return AnswerResponse.builder()
				.id(entity.getId())
				.questionId(entity.getQuestion().getId())
				.content(entity.getContent())
				.createDate(entity.getCreateDate())
				.modifyDate(entity.getModifyDate())
				.author(UserResponse.fromEntity(entity.getAuthor()))
				.voterCount(entity.getVoterSet().size())
				.commentList(CommentResponse.getCommentResponses(entity.getCommentList()))
				.build();
	}
	
	public static List<AnswerResponse> getAnswerRequests(List<Answer> answerList){
		return answerList.stream().map(AnswerResponse::fromEntity).toList();
	}
}
