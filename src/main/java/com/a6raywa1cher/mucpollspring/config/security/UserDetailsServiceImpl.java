package com.a6raywa1cher.mucpollspring.config.security;

import com.a6raywa1cher.mucpollspring.dao.repository.UserRepository;
import com.a6raywa1cher.mucpollspring.models.sql.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserDetailsServiceImpl implements UserDetailsService {
	private UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.getByUsername(username);
		if (optionalUser.isEmpty()) {
			throw new UsernameNotFoundException(String.format("Username %s not found", username));
		}
		return new UserDetailsImpl(optionalUser.get());
	}
}
