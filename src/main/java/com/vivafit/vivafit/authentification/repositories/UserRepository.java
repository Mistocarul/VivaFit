package com.vivafit.vivafit.authentification.repositories;

import com.vivafit.vivafit.authentification.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.role = :role ORDER BY u.id ASC")
    Optional<User> findFirstByRole(@Param("role") String role);
}
