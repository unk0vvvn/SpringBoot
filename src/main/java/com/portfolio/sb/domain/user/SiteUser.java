package com.portfolio.sb.domain.user;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.portfolio.sb.domain.BaseEntity;
import com.portfolio.sb.domain.answer.Answer;
import com.portfolio.sb.domain.comment.Comment;
import com.portfolio.sb.domain.question.Question;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
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
@Table(name = "SITE_USER")
public class SiteUser {
	private Long id;
	
	@CreationTimestamp
	private LocalDateTime createDate; 
	
	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	@Builder.Default
	@OneToMany(mappedBy="author", cascade=CascadeType.REMOVE)
	private List<Question> questions = new LinkedList<>();
	
	@Builder.Default
	@OneToMany(mappedBy="author", cascade=CascadeType.REMOVE)
	private List<Answer> answers = new LinkedList<>();
	
	@Builder.Default
	@OneToMany(mappedBy="author", cascade=CascadeType.REMOVE)
	private List<Comment> comments = new LinkedList<>();
}
