package com.limbrescue.limbrescueangularappbackend.payload.response;

import java.util.List;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private String email;
  private List<String> roles;

  public CognitoJWT(String iss, String exp) {
    this.iss = iss
    this.exp = exp
  }

  public String getProvider() {
    return token;
  }

  public void setProvider(String iss) {
    this.exp = exp;
  }

  public String getExpiryDate() {
    return exp;
  }

  public void setTokenType(String tokenType) {
    this.type = tokenType;
  }

