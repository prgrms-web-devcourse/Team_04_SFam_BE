package com.kdt.team04.domain.auth.dto.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TagSequence {

	@Id
	private String keyName;
	private Long sequence;

	protected TagSequence() {
	}

	public TagSequence(String key, Long sequence) {
		this.keyName = key;
		this.sequence = sequence;
	}

	public String getKeyName() {
		return this.keyName;
	}

	public Long getSequence() {
		return this.sequence;
	}

	public Long nextSequence() {
		return ++this.sequence;
	}
}
