package com.mysite.sbb.question;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Question{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=200)
	private String subject;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	
	@Builder.Default
	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	private List<Answer> answerList = new LinkedList<Answer>();
	
	@ManyToOne
	private SiteUser author;
	
	@Builder.Default
	@ManyToMany
	private Set<SiteUser> voterSet = new HashSet<SiteUser>();
	
	@Builder.Default
	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	private List<Comment> commentList = new LinkedList<Comment>();
	
	@Builder.Default
	private Integer viewCount = 0;
}
