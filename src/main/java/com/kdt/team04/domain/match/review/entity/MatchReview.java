package com.kdt.team04.domain.match.review.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.BaseEntity;
import com.kdt.team04.domain.match.post.entity.Match;
import com.kdt.team04.domain.user.entity.User;

@Entity
public class MatchReview extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_id")
	private Match match;

	@Enumerated(value = EnumType.STRING)
	private MatchReviewValue review;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	protected MatchReview() {/*no-op*/}

	public MatchReview(Long id, Match match, MatchReviewValue review, User user) {
		this.id = id;
		this.match = match;
		this.review = review;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public Match getMatch() {
		return match;
	}

	public MatchReviewValue getReview() {
		return review;
	}

	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("match", match)
			.append("review", review)
			.append("user", user)
			.toString();
	}
}
