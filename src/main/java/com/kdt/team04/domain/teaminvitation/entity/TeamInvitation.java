package com.kdt.team04.domain.teaminvitation.entity;

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
import javax.persistence.UniqueConstraint;

import com.kdt.team04.domain.BaseEntity;
import com.kdt.team04.domain.team.entity.Team;
import com.kdt.team04.domain.user.entity.User;

import lombok.Builder;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "team_id", "target_id" }) })
public class TeamInvitation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_id")
	private User target;

	@Enumerated(value = EnumType.STRING)
	private InvitationStatus status;

	protected TeamInvitation() {
	}

	public TeamInvitation(Team team, User target, InvitationStatus status) {
		this(null, team, target, status);
	}

	@Builder
	public TeamInvitation(Long id, Team team, User target, InvitationStatus status) {
		this.id = id;
		this.team = team;
		this.target = target;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public Team getTeam() {
		return team;
	}

	public User getTarget() {
		return target;
	}

	public InvitationStatus getStatus() {
		return status;
	}

	public void accept() {
		this.status = InvitationStatus.ACCEPTED;
	}

	public void refuse() {
		this.status = InvitationStatus.REFUSED;
	}

}
