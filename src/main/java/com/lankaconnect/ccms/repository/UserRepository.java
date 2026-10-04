package com.lankaconnect.ccms.repository;

import com.lankaconnect.ccms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByRoleAndActive(String role, boolean active);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

