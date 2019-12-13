package com.a6raywa1cher.mucpollspring.rest.mirror;

import com.a6raywa1cher.mucpollspring.models.User;
import com.a6raywa1cher.mucpollspring.models.UserStatus;
import lombok.Data;

@Data
public class UserMirror {
	private Long id;

	private String username;

	private UserStatus status;

	public static UserMirror convert(User user) {
		UserMirror userMirror = new UserMirror();
		userMirror.setId(user.getId());
		userMirror.setUsername(user.getUsername());
		userMirror.setStatus(user.getStatus());
		return userMirror;
	}
}
