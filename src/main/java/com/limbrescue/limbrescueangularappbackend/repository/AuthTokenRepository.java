package com.limbrescue.limbrescueangularappbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.limbrescue.limbrescueangularappbackend.models.AuthToken;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, int> {
  Optional<User> findById(int id);
  Boolean existsById();
}