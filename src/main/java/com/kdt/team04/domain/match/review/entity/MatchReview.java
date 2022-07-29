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
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.User;

import lombok.Builder;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_user_id")
	private User targetUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_team_id")
	private Team targetTeam;

	protected MatchReview() {/*no-op*/}

	@Builder
	public MatchReview(Long id, Match match, MatchReviewValue review, User user, Team team, User targetUser,
		Team targetTeam) {
		this.id = id;
		this.match = match;
		this.review = review;
		this.user = user;
		this.team = team;
		this.targetUser = targetUser;
		this.targetTeam = targetTeam;
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

	public Team getTeam() {
		return team;
	}

	public User getTargetUser() {
		return targetUser;
	}

	public Team getTargetTeam() {
		return targetTeam;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("match", match)
			.append("review", review)
			.append("user", user)
			.append("team", team)
			.append("targetUser", targetUser)
			.append("targetTeam", targetTeam)
			.toString();
	}
}
