package com.a6raywa1cher.mucpollspring.config.security;

import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.TagRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.UserRepository;
import com.a6raywa1cher.mucpollspring.models.sql.Poll;
import com.a6raywa1cher.mucpollspring.models.sql.Tag;
import com.a6raywa1cher.mucpollspring.models.sql.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.stream.Collectors;

public class UserDetailsServiceImpl implements UserDetailsService {
	private UserRepository userRepository;
	private PollRepository pollRepository;
	private TagRepository tagRepository;

	public UserDetailsServiceImpl(UserRepository userRepository, PollRepository pollRepository,
	                              TagRepository tagRepository) {
		this.userRepository = userRepository;
		this.pollRepository = pollRepository;
		this.tagRepository = tagRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.getByUsername(username);
		if (optionalUser.isEmpty()) {
			throw new UsernameNotFoundException(String.format("Username %s not found", username));
		}
		User user = optionalUser.get();
		return new UserDetailsImpl(user,
				pollRepository.getAllByCreatorId(user.getId()).stream()
						.map(Poll::getId)
						.collect(Collectors.toList()),
				tagRepository.getAllByCreator(user).stream()
						.map(Tag::getId)
						.collect(Collectors.toList())
		);
	}
}
