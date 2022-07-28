package com.kdt.team04.domain.matches.request.entity;

import static com.kdt.team04.domain.matches.request.entity.MatchRequestStatus.WAITING;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

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
import com.kdt.team04.domain.matches.match.entity.Match;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.User;

@Entity
public class MatchRequest extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_id")
	private Match match;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@Enumerated(value = EnumType.STRING)
	private MatchRequestStatus status;

	protected MatchRequest() {/*no-op*/}

	public MatchRequest(Long id, Match match, String content, User user, Team team, MatchRequestStatus status) {
		this.id = id;
		this.match = match;
		this.content = content;
		this.user = user;
		this.team = team;
		this.status = defaultIfNull(status, WAITING);
	}

	public Long getId() {
		return id;
	}

	public Match getMatch() {
		return match;
	}

	public String getContent() {
		return content;
	}

	public User getUser() {
		return user;
	}

	public Team getTeam() {
		return team;
	}

	public MatchRequestStatus getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("match", match)
			.append("content", content)
			.append("user", user)
			.append("team", team)
			.append("status", status)
			.toString();
	}
}
