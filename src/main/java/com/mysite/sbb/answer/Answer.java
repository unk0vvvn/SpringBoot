package com.mysite.sbb.answer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.question.Question;
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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Answer{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(columnDefinition="TEXT")
	private String content;
	
	private LocalDateTime createDate; 
	private LocalDateTime modifyDate;
	
	@ManyToOne
	private Question question;
	
	@ManyToOne
	private SiteUser author;
	
	@Builder.Default
	@ManyToMany
	private Set<SiteUser> voterSet = new HashSet<SiteUser>();
	
	@Builder.Default
	@OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
	private List<Comment> commentList = new LinkedList<Comment>();
}
