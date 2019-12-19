package com.a6raywa1cher.mucpollspring.rest;

import com.a6raywa1cher.mucpollspring.rest.mirror.UserMirror;
import com.a6raywa1cher.mucpollspring.rest.request.UserRegistrationRequest;
import com.a6raywa1cher.mucpollspring.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	private UserService userService;

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/reg")
	public ResponseEntity<UserMirror> register(@RequestBody @Valid UserRegistrationRequest dto) {
		return ResponseEntity.ok(
				UserMirror.convert(userService.registerUser(dto.getUsername(), dto.getPassword())));
	}

	@GetMapping("/cookies")
//	@Secured("ROLE_USER")
	public ResponseEntity<String> safeZone() {
		String text = "[" + System.currentTimeMillis() + "]:" + "cookies!";
		template.convertAndSend("/topic/cookies", text);
		return ResponseEntity.ok("COOKIES!");
	}
}
