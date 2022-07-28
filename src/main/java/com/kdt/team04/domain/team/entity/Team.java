package com.kdt.team04.domain.team.entity;

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
import com.kdt.team04.domain.team.SportsCategory;
import com.kdt.team04.domain.user.entity.User;

import lombok.Builder;

@Entity
public class Team extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String description;

	@Enumerated(value = EnumType.STRING)
	private SportsCategory sportsCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leader_id")
	private User leader;

	protected Team() {
	}

	public Team(String name, String description, SportsCategory sportsCategory, User leader) {
		this(null, name, description, sportsCategory, leader);
	}

	@Builder
	public Team(Long id, String name, String description, SportsCategory sportsCategory, User leader) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.sportsCategory = sportsCategory;
		this.leader = leader;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public SportsCategory getSportsCategory() {
		return sportsCategory;
	}

	public User getLeader() {
		return leader;
	}
}
