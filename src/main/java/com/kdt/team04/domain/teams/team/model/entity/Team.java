package com.kdt.team04.domain.teams.team.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.BaseEntity;
import com.kdt.team04.domain.teams.team.model.SportsCategory;
import com.kdt.team04.domain.user.entity.User;

import lombok.Builder;

@Entity
public class Team extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(min = 2, max = 10)
	@Column(unique = true)
	private String name;

	@NotBlank
	@Size(max = 100)
	private String description;

	@NotNull
	@Enumerated(value = EnumType.STRING)
	private SportsCategory sportsCategory;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leader_id", nullable = false)
	private User leader;

	private String logoImageUrl;

	protected Team() {
	}

	@Builder
	public Team(Long id, String name, String description, SportsCategory sportsCategory,
		User leader, String logoImageUrl) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.sportsCategory = sportsCategory;
		this.leader = leader;
		this.logoImageUrl = logoImageUrl;
	}

	public void updateLogoUrl(String logoImageUrl) {
		this.logoImageUrl = logoImageUrl;
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

	public String getLogoImageUrl() {
		return logoImageUrl;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("name", name)
			.append("description", description)
			.append("sportsCategory", sportsCategory)
			.append("leader", leader)
			.append("baseEntity",super.toString())
			.toString();
	}
}
