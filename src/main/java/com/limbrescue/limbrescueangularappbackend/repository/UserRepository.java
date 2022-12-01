package com.limbrescue.limbrescueangularappbackend.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.limbrescue.limbrescueangularappbackend.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  List<User> findAll();
  Boolean existsByUsername(String username);

}
