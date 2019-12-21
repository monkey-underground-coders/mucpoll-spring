package com.a6raywa1cher.mucpollspring.stomp;

import com.a6raywa1cher.mucpollspring.models.file.PollSession;
import com.a6raywa1cher.mucpollspring.service.interfaces.VotingService;
import com.a6raywa1cher.mucpollspring.stomp.response.CurrentSessionInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DisconnectApplicationListener implements ApplicationListener<SessionDisconnectEvent> {
	private static final Logger log = LoggerFactory.getLogger(DisconnectApplicationListener.class);
	private Map<String, Boolean> handling;
	private VotingService votingService;
	private SimpMessagingTemplate template;

	@Autowired
	public DisconnectApplicationListener(VotingService votingService, SimpMessagingTemplate template) {
		this.votingService = votingService;
		this.template = template;
		this.handling = new ConcurrentHashMap<>();
	}

	private synchronized boolean check(SessionDisconnectEvent event) {
		if (event.getUser() == null || event.getUser().getName() == null || handling.containsKey(event.getUser().getName())) {
			return false;
		}
		handling.put(event.getUser().getName(), true);
		return true;
	}

	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		String name = (event.getUser() == null ? "unknown" : event.getUser().getName());
		log.debug("User disconnected, username: " + name);
		if (!check(event)) {
			return;
		}
		assert event.getUser() != null;
		log.info("Starting closing votes for username " + name);
		try {
			List<PollSession> pollSessions = votingService.closeAllVotesByUser(name);
			for (PollSession ps : pollSessions) {
				template.convertAndSend(String.format("/topic/%d/%s", ps.getPid(), ps.getSid()),
						new CurrentSessionInfoResponse(ps));
			}
		} finally {
			log.info("Completed closing votes for username " + name);
			handling.remove(name);
		}
	}
}
