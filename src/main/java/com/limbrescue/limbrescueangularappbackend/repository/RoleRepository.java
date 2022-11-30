package com.limbrescue.limbrescueangularappbackend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.limbrescue.limbrescueangularappbackend.models.ERole;
import com.limbrescue.limbrescueangularappbackend.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
