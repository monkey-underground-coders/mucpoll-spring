package com.a6raywa1cher.mucpollspring.config;

import com.a6raywa1cher.mucpollspring.config.security.CookieAuthFilter;
import com.a6raywa1cher.mucpollspring.config.security.UserDetailsServiceImpl;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.PollRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.TagRepository;
import com.a6raywa1cher.mucpollspring.dao.repository.sql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private UserRepository userRepository;
	private PollRepository pollRepository;
	private TagRepository tagRepository;

	@Autowired
	public SecurityConfig(UserRepository userRepository, PollRepository pollRepository, TagRepository tagRepository) {
		this.userRepository = userRepository;
		this.pollRepository = pollRepository;
		this.tagRepository = tagRepository;
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
				.antMatchers("/").permitAll()
				.antMatchers("/user/reg").permitAll()
//				.antMatchers("/user/cookies").anonymous()
				.antMatchers("/v2/api-docs", "/webjars/**", "/swagger-resources", "/swagger-resources/**", "/swagger-ui.html").permitAll()
				.antMatchers("/csrf").permitAll()
//				.antMatchers("/poll").anonymous()
				.antMatchers("/poll").permitAll()
				.anyRequest().authenticated()
				.and()
				.httpBasic()
				.and()
				.cors();
		http.addFilterBefore(new CookieAuthFilter(), BasicAuthenticationFilter.class);
	}

	@Override
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl(userRepository, pollRepository, tagRepository);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource(AppConfigProperties appConfigProperties) {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(appConfigProperties.getCorsAllowedOrigins()));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "HEAD"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
