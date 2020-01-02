package com.a6raywa1cher.mucpollspring.service.impl;

import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.TagRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.UserRepository;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.Tag;
import com.a6raywa1cher.mucpollspring.models.sql.User;
import com.a6raywa1cher.mucpollspring.service.exceptions.UserNotFoundException;
import com.a6raywa1cher.mucpollspring.service.interfaces.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {
	private TagRepository tagRepository;
	private UserRepository userRepository;
	private PollRepository pollRepository;

	@Autowired
	public TagServiceImpl(TagRepository tagRepository, UserRepository userRepository, PollRepository pollRepository) {
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
		this.pollRepository = pollRepository;
	}

	@Override
	public Tag createTag(Poll poll, String name, User user) {
		Tag tag = new Tag();
		tag.setName(name);
		tag.setPollList(Collections.singletonList(poll));
		tag.setCreator(user);
		Tag saved = tagRepository.save(tag);
		poll.getTags().add(saved);
		pollRepository.save(poll);
		return saved;
	}

	@Override
	public Tag editTag(Tag tag, String name) {
		tag.setName(name);
		return tagRepository.save(tag);
	}

	@Override
	public void deleteTag(Tag tag) {
		tagRepository.delete(tag);
	}

	@Override
	public Optional<Tag> getById(Long tagId) {
		return tagRepository.findById(tagId);
	}

	@Override
	public List<Tag> getAllByUser(Long userId) throws UserNotFoundException {
		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isEmpty()) {
			throw new UserNotFoundException(userId);
		}
		return tagRepository.getAllByCreator(optionalUser.get());
	}
}
