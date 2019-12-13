package com.a6raywa1cher.mucpollspring.config;

import com.a6raywa1cher.mucpollspring.config.security.UserDetailsServiceImpl;
import com.a6raywa1cher.mucpollspring.dao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private UserRepository repository;

	@Autowired
	public SecurityConfig(UserRepository repository) {
		this.repository = repository;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()
				.antMatchers("/user/reg").permitAll()
				.antMatchers("/v2/api-docs", "/webjars/**", "/swagger-resources", "/swagger-resources/**", "/swagger-ui.html").permitAll()
				.antMatchers("/csrf").permitAll()
				.anyRequest().authenticated()
				.and()
				.httpBasic();
	}

	@Override
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl(repository);
	}
}
