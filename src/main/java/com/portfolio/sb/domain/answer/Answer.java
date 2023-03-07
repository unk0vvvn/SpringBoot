package com.portfolio.sb.domain.answer;

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
import com.portfolio.sb.domain.comment.Comment;
import com.portfolio.sb.domain.question.Question;
import com.portfolio.sb.domain.user.SiteUser;

import java.util.HashSet;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter

@DiscriminatorValue("ANSWER")
@PrimaryKeyJoinColumn(name = "ANSWER_ID")

@Entity
@Table(name="ANSWER")
public class Answer extends BaseEntity{
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private Question question;
	
	@Builder.Default
	private Set<Integer> voters = new HashSet<Integer>();
	
	@Builder.Default
	@Fetch(FetchMode.SUBSELECT)
	@OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE)
	private List<Comment> comments = new LinkedList<Comment>();
}
