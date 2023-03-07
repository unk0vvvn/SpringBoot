package com.portfolio.sb.domain.comment.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.portfolio.sb.domain.answer.Answer;
import com.portfolio.sb.domain.comment.Comment;
import com.portfolio.sb.domain.question.Question;
import com.portfolio.sb.domain.user.SiteUser;
import com.portfolio.sb.domain.user.dto.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommentResponse {
	private Long id;
	private Long questionId;
	private Long answerId;
	
	private UserResponse author;
	
	private String content;
	
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	
	public static CommentResponse fromEntity(Comment comment) {
		Long questionId = null;
		Long answerId = null;
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
	
//	public Comment toEntity(Question question, Answer answer, SiteUser author) {
//		return Comment.builder()
//				.id(id)
//				.author(author)
//				.content(content)
//				.createDate(createDate)
//				.modifyDate(modifyDate)
//				.question(question)
//				.answer(answer)
//				.build();
//	}
	
	
	public static List<CommentResponse> getCommentResponses(List<Comment> commentList){
		return commentList.stream().map(CommentResponse::fromEntity).toList();
	}
}
