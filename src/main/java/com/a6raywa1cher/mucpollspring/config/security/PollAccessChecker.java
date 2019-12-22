package com.a6raywa1cher.mucpollspring.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class PollAccessChecker {
	public boolean check(Authentication authentication, Long pid) {
		PollGrantedAuthority toFind = new PollGrantedAuthority(pid);
		return authentication.getAuthorities().contains(toFind);
	}
}
