package com.kdt.team04.domain.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdt.team04.domain.auth.entity.TagSequence;

@Service
public class TagSequenceService {

	private final TagSequenceRepository tagSequenceRepository;

	public TagSequenceService(TagSequenceRepository tagSequenceRepository) {
		this.tagSequenceRepository = tagSequenceRepository;
	}

	@Transactional
	public Long nextSequenceByKey(String key) {
		Optional<TagSequence> foundSequence = tagSequenceRepository.findById(key);
		if (foundSequence.isPresent()) {
			return foundSequence.get().nextSequence();
		} else {
			TagSequence newTagSequence = new TagSequence(key, 0L);
			tagSequenceRepository.save(newTagSequence);

			return 0L;
		}
	}
}
