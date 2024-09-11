package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.login = :login")
    boolean existsByLogin(@Param("login") String login);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.login = :login AND u.password = :key")
    boolean validateKey(@Param("login") String login, @Param("key") String key);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
    boolean validateEmail(@Param("email") String email);

    @Query("SELECT u User FROM User u WHERE u.login = :login")
    Optional<User> findByLogin(@Param("login") String login);

}
