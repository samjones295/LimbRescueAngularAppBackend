package com.limbrescue.limbrescueangularappbackend.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.limbrescue.limbrescueangularappbackend.security.jwt.JwtUtils;
import com.limbrescue.limbrescueangularappbackend.service.UserService;
import com.limbrescue.limbrescueangularappbackend.models.User;

@Component
public class JwtRequestFilter {/*
                                * extends OncePerRequestFilter {
                                * 
                                * // @Autowired
                                * // private UserService userDetailsService;
                                * 
                                * private UserDetailsService userDetailsService;
                                * 
                                * @Autowired
                                * private JwtUtils jwtUtil;
                                * 
                                * @Override
                                * protected void doFilterInternal(HttpServletRequest request,
                                * HttpServletResponse response, FilterChain chain)
                                * throws ServletException, IOException {
                                * 
                                * final String authorizationHeader = request.getHeader("Cookie");
                                * 
                                * String username = null;
                                * String jwt = null;
                                * 
                                * if (authorizationHeader != null &&
                                * authorizationHeader.startsWith("auth_jwt=")) {
                                * jwt = authorizationHeader.substring(9);
                                * username = jwtUtil.getUserNameFromJwtToken(jwt);
                                * System.out.println("Authentication!" + jwt);
                                * 
                                * }
                                * 
                                * if (username != null &&
                                * SecurityContextHolder.getContext().getAuthentication() == null) {
                                * 
                                * UserDetails userDetails =
                                * this.userDetailsService.loadUserByUsername(username);
                                * if (jwtUtil.validateJwtToken(jwt)) {
                                * 
                                * UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new
                                * UsernamePasswordAuthenticationToken(
                                * userDetails, null, userDetails.getAuthorities());
                                * 
                                * usernamePasswordAuthenticationToken.setDetails(
                                * new WebAuthenticationDetailsSource().buildDetails(request));
                                * SecurityContextHolder.getContext().setAuthentication(
                                * usernamePasswordAuthenticationToken);
                                * }
                                * 
                                * }
                                * chain.doFilter(request, response);
                                * 
                                * }
                                */
}
