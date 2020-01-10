package com.a6raywa1cher.mucpollspring.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

// https://stackoverflow.com/questions/45405332/websocket-authentication-and-authorization-in-spring
@Configuration
public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
	@Override
	protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
		// You can customize your authorization mapping here.
		messages.anyMessage().permitAll();
	}

	// TODO: For test purpose (and simplicity) i disabled CSRF, but you should re-enable this and provide a CRSF endpoint.
	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}
}