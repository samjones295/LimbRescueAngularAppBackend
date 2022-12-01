package com.limbrescue.limbrescueangularappbackend.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;
import java.sql.Timestamp;
import java.sql.*;
import java.util.*;
import java.util.Optional;

import javax.validation.Valid;
import javax.servlet.http.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.limbrescue.limbrescueangularappbackend.aws_cognito.CognitoJWTParser;
import com.limbrescue.limbrescueangularappbackend.aws_cognito.CognitoHelper;
import com.limbrescue.limbrescueangularappbackend.models.CognitoJWT;
import com.limbrescue.limbrescueangularappbackend.models.User;
import com.limbrescue.limbrescueangularappbackend.models.AuthToken;
import com.limbrescue.limbrescueangularappbackend.payload.request.LoginRequest;
import com.limbrescue.limbrescueangularappbackend.payload.request.SignupRequest;
import com.limbrescue.limbrescueangularappbackend.payload.response.AuthenticationResponse;
import com.limbrescue.limbrescueangularappbackend.payload.response.MessageResponse;
import com.limbrescue.limbrescueangularappbackend.service.UserServiceImpl;
import com.limbrescue.limbrescueangularappbackend.service.AuthTokenServiceImpl;
import com.limbrescue.limbrescueangularappbackend.security.jwt.JwtUtils;
import com.limbrescue.limbrescueangularappbackend.security.encryption.AES;
import com.limbrescue.limbrescueangularappbackend.security.cookie.AuthTokenCookie;

import com.fasterxml.jackson.databind.ObjectMapper;



@CrossOrigin(origins = "http://localhost:8081", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  UserServiceImpl userService;

  @Autowired
  AuthTokenServiceImpl authtokenService;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  CognitoHelper cognitoHelper;



  @PostMapping("/signin")
  public void authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) throws Exception {
  
    ObjectMapper cognitoMapper = new ObjectMapper();
    String username = loginRequest.getUsername();
    String password = loginRequest.getPassword();
    String cognito_jwt = cognitoHelper.validateUser(username, password);
    CognitoJWT payload = cognitoMapper.readValue(CognitoJWTParser.getPayload(cognito_jwt).toString(), CognitoJWT.class);
    String iss = payload.getIss();
    String credentials = cognitoHelper.getCredentials(iss, cognito_jwt).toString();
    String uuid = UUID.randomUUID().toString();
    String exp = payload.getExp();
    String key = authtokenService.insertToken(uuid, credentials, exp, username);
    String subject = new AuthenticationResponse(uuid, key).toString();
    String auth_jwt = jwtUtils.generateJwtToken(subject);
    AuthTokenCookie authTokenCookie = new AuthTokenCookie(auth_jwt);
    response.addCookie(authTokenCookie);
  }

    @PostMapping("/signup")
  public void authenticateUser(@Valid @RequestBody SignupRequest signupRequest, HttpServletResponse response) throws Exception {

        CognitoHelper cognitoHelper = new CognitoHelper();
        String uuid = UUID.randomUUID().toString();
        Boolean success = cognitoHelper.SignUpUser(signupRequest.getUsername(), signupRequest.getPassword(), signupRequest.getGivenname(), signupRequest.Middlename(), signupRequest.getFamilyname());
        if(success){
            this.insertUser(uuid, signupRequest.getUsername(), signupRequest.getGivenname(), signupRequest.Middlename(), signupRequest.getFamilyname());
        } 
  }


}