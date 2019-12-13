package com.a6raywa1cher.mucpollspring.service.impl;

import com.a6raywa1cher.mucpollspring.dao.repository.UserRepository;
import com.a6raywa1cher.mucpollspring.models.User;
import com.a6raywa1cher.mucpollspring.models.UserStatus;
import com.a6raywa1cher.mucpollspring.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	private UserRepository repository;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public User registerUser(String username, String password) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setStatus(UserStatus.ACTIVE);
		return repository.save(user);
	}
}
