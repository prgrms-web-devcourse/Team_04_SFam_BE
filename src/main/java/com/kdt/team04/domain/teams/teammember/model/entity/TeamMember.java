package com.kdt.team04.domain.teams.teammember.model.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.kdt.team04.domain.BaseEntity;
import com.kdt.team04.domain.teams.team.model.entity.Team;
import com.kdt.team04.domain.teams.teammember.model.TeamMemberRole;
import com.kdt.team04.domain.user.entity.User;

@Entity
public class TeamMember extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	private TeamMemberRole role;

	protected TeamMember() {
	}

	public TeamMember(Team team, User user, TeamMemberRole role) {
		this.team = team;
		this.user = user;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public Team getTeam() {
		return team;
	}

	public User getUser() {
		return user;
	}

	public TeamMemberRole getRole() {
		return role;
	}

}
