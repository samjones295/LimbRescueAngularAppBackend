package com.limbrescue.limbrescueangularappbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.limbrescue.limbrescueangularappbackend.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class ApplicationConfig {

    private UserRepository repository;

    @Bean
    public UserDetailsService userDetailsService() {
        /*
         * return new UserDetailsService() {
         * 
         * @Override
         * public UserDetails loadUserByUsername(String username) throws
         * UsernameNotFoundException {
         * return null;
         * }
         */
        return username -> repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    };
}
