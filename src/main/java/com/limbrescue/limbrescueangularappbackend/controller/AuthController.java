package com.limbrescue.limbrescueangularappbackend.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.limbrescue.limbrescueangularappbackend.models.ERole;
import com.limbrescue.limbrescueangularappbackend.models.Role;
import com.limbrescue.limbrescueangularappbackend.models.User;
import com.limbrescue.limbrescueangularappbackend.payload.request.LoginRequest;
import com.limbrescue.limbrescueangularappbackend.payload.request.SignupRequest;
import com.limbrescue.limbrescueangularappbackend.payload.response.JwtResponse;
import com.limbrescue.limbrescueangularappbackend.payload.response.MessageResponse;
import com.limbrescue.limbrescueangularappbackend.repository.RoleRepository;
import com.limbrescue.limbrescueangularappbackend.repository.UserRepository;
import com.limbrescue.limbrescueangularappbackend.security.jwt.JwtUtils;
import com.limbrescue.limbrescueangularappbackend.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  /**
     * Authorizes the login.
     *
     * @return
     *          The authentication bean.
     */
    @GetMapping(path = "/basicauth")
    public void helloWorldBean() {
        System.out.println("You are authenticated");
    }

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
  
    
    String result = cognitoHelper.validateUser(loginRequest.getUsername(), loginRequest.getPassword());
        
    String provider = CognitoJWTParser.getClaim(payload, "iss");
    String expiryDate = CognitoJWTParser.getClaim(payload, "exp");  
    String credentials = cognitoHelper.getCredentials(provider, result).toString();
    String key = this.InsertHashedToken(uuid, username, credentials, expiryDate);
    AuthenticationResponse subject = new AuthenticationResponse(uuid, key);

    String jwt = jwtUtils.generateJwtToken(authentication);


    return ResponseEntity.ok(new JwtResponse());
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(), 
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "mod":
          Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}