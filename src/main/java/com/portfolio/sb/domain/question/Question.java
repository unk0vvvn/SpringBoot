package com.portfolio.sb.domain.question;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import com.portfolio.sb.domain.BaseEntity;
import com.portfolio.sb.domain.answer.Answer;
import com.portfolio.sb.domain.comment.Comment;
import com.portfolio.sb.domain.user.SiteUser;

import java.util.HashSet;


import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter

@DiscriminatorValue("QUESTION")
@PrimaryKeyJoinColumn(name = "QUESTION_ID")

@Entity
@Table(name="QUESTION")
public class Question extends BaseEntity{
	
	@Column(length=200, nullable=false)
	private String subject;
	
	@Builder.Default
	private Integer viewCount = 0;
	
	@Builder.Default
	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	private List<Answer> answers = new LinkedList<Answer>();
	
	@Builder.Default
	private Set<Integer> voters = new HashSet<Integer>();
	
	@Builder.Default
	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
	private List<Comment> comments = new LinkedList<Comment>();
	
	
}
