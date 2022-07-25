package com.kdt.team04.domain.match.request.entity;

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

@Entity
public class MatchChat extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "match_request_id")
	private MatchRequest request;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_id")
	private User target;

	private String content;

	protected MatchChat() {/*no-op*/}

	public MatchChat(Long id, MatchRequest request, User user, User target, String content) {
		this.id = id;
		this.request = request;
		this.user = user;
		this.target = target;
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public MatchRequest getRequest() {
		return request;
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

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("id", id)
			.append("request", request)
			.append("user", user)
			.append("target", target)
			.append("content", content)
			.toString();
	}
}
