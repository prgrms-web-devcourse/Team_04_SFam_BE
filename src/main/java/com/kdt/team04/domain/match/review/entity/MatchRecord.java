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
public class MatchRecord extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_id")
	private Match match;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@Enumerated(value = EnumType.STRING)
	private MatchRecordValue result;

	protected MatchRecord() {/*no-op*/}

	@Builder
	public MatchRecord(Long id, Match match, User user, Team team, MatchRecordValue result) {
		this.id = id;
		this.match = match;
		this.user = user;
		this.team = team;
		this.result = result;
	}

	public Long getId() {
		return id;
	}

	public Match getMatch() {
		return match;
	}

	public User getUser() {
		return user;
	}

	public Team getTeam() {
		return team;
	}

	public MatchRecordValue getResult() {
		return result;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("match", match)
			.append("user", user)
			.append("team", team)
			.append("result", result)
			.toString();
	}
}
