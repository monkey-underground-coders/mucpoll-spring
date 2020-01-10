package com.a6raywa1cher.mucpollspring.config.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationManagerImpl implements AuthenticationManager {
	private UserDetailsService userDetailsService;
	private PasswordEncoder passwordEncoder;

	public AuthenticationManagerImpl(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!(authentication instanceof UsernamePasswordAuthenticationToken) ||
				!(authentication.getPrincipal() instanceof String) ||
				!(authentication.getCredentials() instanceof String)) {
			return null;
		}
		UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
		UserDetails userDetails;
		try {
			userDetails = userDetailsService.loadUserByUsername((String) token.getPrincipal());
		} catch (UsernameNotFoundException e) {
			return null;
		}
		if (!passwordEncoder.matches((String) authentication.getCredentials(), userDetails.getPassword())) {
			throw new BadCredentialsException("Not matching password");
		}
		return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
	}
}
