package com.a6raywa1cher.mucpollspring.service.interfaces;

import com.a6raywa1cher.mucpollspring.models.sql.User;

import java.util.Optional;

public interface UserService {
	User registerUser(String username, String password);

	Optional<User> getByUsername(String username);
}
