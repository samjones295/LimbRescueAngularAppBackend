package com.limbrescue.limbrescueangularappbackend.service;
import com.limbrescue.limbrescueangularappbackend.repository.AuthTokenRepository;
import com.limbrescue.limbrescueangularappbackend.models.AuthToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional()
public class UserServiceImpl implements UserService {

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private AuthTokenRepository authtoken;


  @Override
  public Optional<AuthToken> findByUsername(int id) {
    return user.findById(id);
  }
}