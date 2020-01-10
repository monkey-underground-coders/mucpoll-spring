//package com.a6raywa1cher.mucpollspring.config.security;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//
//// https://stackoverflow.com/questions/54609570/how-to-do-basic-authentication-using-cookies-in-spring-security
//@Slf4j
//public class CookieAuthFilter extends OncePerRequestFilter {
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//		MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest((HttpServletRequest) request);
//
//		Cookie[] cookies = request.getCookies();
//		log.info("Processing request... " + (cookies != null ? cookies.length : 0) + " cookies");
//		if (cookies != null && cookies.length > 0) {
//			for (Cookie cookie : cookies) {
//				if (cookie.getName().equalsIgnoreCase("auth")) {
//					log.info("Putting Authorization: " + URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));
//					mutableRequest.putHeader("Authorization",
//							URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));
//				}
//			}
//		}
//		filterChain.doFilter(mutableRequest, response);
//	}
//}
