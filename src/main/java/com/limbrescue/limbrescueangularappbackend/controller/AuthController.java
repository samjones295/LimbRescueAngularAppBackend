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
import com.limbrescue.limbrescueangularappbackend.payload.request.LoginRequest;
import com.limbrescue.limbrescueangularappbackend.payload.request.SignupRequest;
import com.limbrescue.limbrescueangularappbackend.payload.response.JwtResponse;
import com.limbrescue.limbrescueangularappbackend.payload.response.MessageResponse;
import com.limbrescue.limbrescueangularappbackend.service.UserServiceImpl;
import com.limbrescue.limbrescueangularappbackend.security.jwt.JwtUtils;
import com.limbrescue.limbrescueangularappbackend.security.encryption.AES;

import com.fasterxml.jackson.databind.ObjectMapper;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  UserServiceImpl userService;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  CognitoHelper cognitoHelper;


  //Inserts Hashed Access Token associated with a user to database
    public String InsertHashedToken(String uuid, String username, String accessToken, String expiryDate) {
        AES aes_encryption = new AES();
        String key = aes_encryption.init();
        String token = aes_encryption.encrypt(accessToken, key);
        Timestamp exp = new Timestamp(Long.parseLong(expiryDate)*1000);
        Timestamp createdat = new Timestamp(System.currentTimeMillis());

            

        return key;
    }

  @PostMapping("/signin")
  public void authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
  
    ObjectMapper cognitoMapper = new ObjectMapper();
    String cognito_jwt = cognitoHelper.validateUser(loginRequest.getUsername(), loginRequest.getPassword());
    CognitoJWT payload = cognitoMapper.readValue(CognitoJWTParser.getPayload(cognito_jwt).toString(), CognitoJWT.class);
    String credentials = cognitoHelper.getCredentials(payload.getIss(), cognito_jwt).toString();
    String uuid = UUID.randomUUID().toString();
    String key = this.InsertHashedToken(uuid, loginRequest.getUsername(), credentials, payload.getExp());

  }


}