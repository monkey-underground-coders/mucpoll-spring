package com.a6raywa1cher.mucpollspring.service.interfaces;

import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.Tag;
import com.a6raywa1cher.mucpollspring.models.sql.User;
import com.a6raywa1cher.mucpollspring.service.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface TagService {
	Tag createTag(Poll poll, String name, User user);

	Tag editTag(Tag tag, String name);

	void deleteTag(Tag tag);

	Optional<Tag> getById(Long tagId);

	List<Tag> getAllByUser(Long userId) throws UserNotFoundException;
}
