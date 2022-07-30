package com.kdt.team04.domain.matches.match.entity;

import static com.kdt.team04.domain.matches.match.entity.MatchStatus.WAITING;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.time.LocalDate;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.BaseEntity;
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.Location;
import com.kdt.team04.domain.user.entity.User;

import lombok.Builder;

@Table(name = "matches")
@Entity
public class Match extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Enumerated(value = EnumType.STRING)
	private SportsCategory sportsCategory;

	@Enumerated(value = EnumType.STRING)
	private MatchType matchType;

	private LocalDate matchDate;

	private String content;

	private int participants;

	@Enumerated(value = EnumType.STRING)
	private MatchStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@Embedded
	private Location location;

	public void setLocation(Location location) {
		this.location = location;
	}

	protected Match() {/*no-op*/}

	@Builder
	public Match(
		Long id, String title, SportsCategory sportsCategory, MatchType matchType, LocalDate matchDate,
		String content, int participants, MatchStatus status, User user, Team team, Location location
	) {
		this.id = id;
		this.title = title;
		this.sportsCategory = sportsCategory;
		this.matchType = matchType;
		this.matchDate = matchDate;
		this.content = content;
		this.participants = participants;
		this.status = defaultIfNull(status, WAITING);
		this.user = user;
		this.team = team;
		this.location = location;
	}

	public void updateStatus(MatchStatus status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public SportsCategory getSportsCategory() {
		return sportsCategory;
	}

	public MatchType getMatchType() {
		return matchType;
	}

	public LocalDate getMatchDate() {
		return matchDate;
	}

	public int getParticipants() {
		return participants;
	}

	public String getContent() {
		return content;
	}

	public MatchStatus getStatus() {
		return status;
	}

	public User getUser() {
		return user;
	}

	public Team getTeam() {
		return team;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("title", title)
			.append("sportsCategory", sportsCategory)
			.append("matchType", matchType)
			.append("matchDate", matchDate)
			.append("content", content)
			.append("status", status)
			.append("user", user)
			.append("team", team)
			.toString();
	}
}
