package com.kdt.team04.domain.team.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.kdt.team04.domain.BaseEntity;
import com.kdt.team04.domain.team.Category;
import com.kdt.team04.domain.user.entity.User;

import lombok.Builder;

@Entity
public class Team extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String teamName;

	private String description;

	private Category sportsCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leader_id")
	private User leader;

	protected Team() {
	}

	@Builder
	public Team(Long id, String teamName, String description, Category sportsCategory, User leader) {
		this.id = id;
		this.teamName = teamName;
		this.description = description;
		this.sportsCategory = sportsCategory;
		this.leader = leader;
	}

	public Long getId() {
		return id;
	}

	public String getTeamName() {
		return teamName;
	}

	public String getDescription() {
		return description;
	}

	public Category getSportsCategory() {
		return sportsCategory;
	}

	public User getLeader() {
		return leader;
	}
}
