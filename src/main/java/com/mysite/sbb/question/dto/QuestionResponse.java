package com.mysite.sbb.question.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedList;

import com.mysite.sbb.answer.dto.AnswerResponse;
import com.mysite.sbb.comment.dto.CommentResponse;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.dto.UserResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class QuestionResponse {
	private Integer id;
	
	private String subject;
	private String content;
	
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	
	private UserResponse author;
	
	private Integer answerCount;
	
	private Integer voterCount;
	
	@Builder.Default
	private List<CommentResponse> commentList = new LinkedList<CommentResponse>();
	
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
				.answerCount(entity.getAnswerList().size())
				.voterCount(entity.getVoterSet().size())
				.commentList(CommentResponse.getCommentResponses(entity.getCommentList()))
				.viewCount(entity.getViewCount())
				.build();
	}
}
