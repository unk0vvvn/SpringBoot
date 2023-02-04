package com.mysite.sbb.user;

import java.util.LinkedList;
import java.util.List;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.question.Question;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class SiteUser {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(unique = true)
	private String username;

	private String password;
	
	@Column(unique = true)
	private String email;
	
	@Builder.Default
	@OneToMany(mappedBy="author", cascade=CascadeType.REMOVE)
	private List<Question> questionList = new LinkedList<>();
	
	@Builder.Default
	@OneToMany(mappedBy="author", cascade=CascadeType.REMOVE)
	private List<Answer> answerList = new LinkedList<>();
	
	@Builder.Default
	@OneToMany(mappedBy="author", cascade=CascadeType.REMOVE)
	private List<Comment> commentList = new LinkedList<>();
}
