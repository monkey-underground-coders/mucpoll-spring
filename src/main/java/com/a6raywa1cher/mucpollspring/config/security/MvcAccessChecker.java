package com.a6raywa1cher.mucpollspring.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class MvcAccessChecker {
	public boolean checkPid(Authentication authentication, Long pid) {
		PollGrantedAuthority toFind = new PollGrantedAuthority(pid);
		return authentication.getAuthorities().contains(toFind);
	}

	public boolean checkTid(Authentication authentication, Long tid) {
		TagGrantedAuthority toFind = new TagGrantedAuthority(tid);
		return authentication.getAuthorities().contains(toFind);
	}
}
