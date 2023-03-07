package com.portfolio.sb.domain.comment;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.portfolio.sb.domain.BaseEntity;
import com.portfolio.sb.domain.answer.Answer;
import com.portfolio.sb.domain.question.Question;
import com.portfolio.sb.domain.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter

@DiscriminatorValue("COMMENT")
@PrimaryKeyJoinColumn(name = "COMMENT_ID")

@Entity
@Table(name="COMMENT")
public class Comment extends BaseEntity {
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Question question;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Answer answer;
}
