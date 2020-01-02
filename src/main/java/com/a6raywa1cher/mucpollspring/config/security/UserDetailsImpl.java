package com.a6raywa1cher.mucpollspring.config.security;

import com.a6raywa1cher.mucpollspring.models.sql.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDetailsImpl implements UserDetails {
	private User user;
	private Set<GrantedAuthority> grantedAuthorities;

	public UserDetailsImpl(User user, List<Long> pids, List<Long> tids) {
		this.user = user;
		grantedAuthorities = new HashSet<>();
		grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		pids.stream()
				.map(PollGrantedAuthority::new)
				.forEach(grantedAuthorities::add);
		tids.stream()
				.map(TagGrantedAuthority::new)
				.forEach(grantedAuthorities::add);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
