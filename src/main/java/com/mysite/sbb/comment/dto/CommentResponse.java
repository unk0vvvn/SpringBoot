package com.mysite.sbb.comment.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.dto.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentResponse {
	private Integer id;
	private Integer questionId;
	private Integer answerId;
	
	private UserResponse author;
	
	private String content;
	
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	
	public static CommentResponse fromEntity(Comment comment) {
		Integer questionId = null;
		Integer answerId = null;
		if(comment.getQuestion() != null)
			questionId = comment.getQuestion().getId();
		else
			answerId = comment.getAnswer().getId();
		
		return CommentResponse.builder()
				.id(comment.getId())
				.questionId(questionId)
				.answerId(answerId)
				.author(UserResponse.fromEntity(comment.getAuthor()))
				.content(comment.getContent())
				.createDate(comment.getCreateDate())
				.modifyDate(comment.getModifyDate())
				.build();
	}
	
	public Comment toEntity(Question question, Answer answer, SiteUser author) {
		return Comment.builder()
				.id(id)
				.author(author)
				.content(content)
				.createDate(createDate)
				.modifyDate(modifyDate)
				.question(question)
				.answer(answer)
				.build();
	}
	
	
	public static List<CommentResponse> getCommentResponses(List<Comment> commentList){
		return commentList.stream().map(CommentResponse::fromEntity).toList();
	}
}
