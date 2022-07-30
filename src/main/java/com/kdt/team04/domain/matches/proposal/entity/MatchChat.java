package com.kdt.team04.domain.matches.proposal.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.kdt.team04.domain.BaseEntity;
import com.kdt.team04.domain.user.entity.User;

import lombok.Builder;

@Entity
public class MatchChat extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_proposal_id")
	private MatchProposal proposal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_id")
	private User target;

	private String content;

	private LocalDateTime chattedAt;

	protected MatchChat() {/*no-op*/}

	@Builder
	public MatchChat(Long id, MatchProposal proposal, User user, User target, String content, LocalDateTime chattedAt) {
		this.id = id;
		this.proposal = proposal;
		this.user = user;
		this.target = target;
		this.content = content;
		this.chattedAt = chattedAt;
	}

	public Long getId() {
		return id;
	}

	public MatchProposal getProposal() {
		return proposal;
	}

	public User getUser() {
		return user;
	}

	public User getTarget() {
		return target;
	}

	public String getContent() {
		return content;
	}

	public LocalDateTime getChattedAt() {
		return chattedAt;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("proposal", proposal)
			.append("user", user)
			.append("target", target)
			.append("content", content)
			.append("chattedAt", chattedAt)
			.toString();
	}
}
