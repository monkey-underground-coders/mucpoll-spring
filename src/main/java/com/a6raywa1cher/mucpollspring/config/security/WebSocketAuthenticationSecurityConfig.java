package com.a6raywa1cher.mucpollspring.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// https://stackoverflow.com/questions/45405332/websocket-authentication-and-authorization-in-spring
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationSecurityConfig implements WebSocketMessageBrokerConfigurer {
	private final AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

	public WebSocketAuthenticationSecurityConfig(AuthChannelInterceptorAdapter authChannelInterceptorAdapter) {
		this.authChannelInterceptorAdapter = authChannelInterceptorAdapter;
	}

	@Override
	public void registerStompEndpoints(final StompEndpointRegistry registry) {
		// Endpoints are already registered on WebSocketConfig, no need to add more.
	}

	@Override
	public void configureClientInboundChannel(final ChannelRegistration registration) {
		registration.interceptors(authChannelInterceptorAdapter);
	}

}