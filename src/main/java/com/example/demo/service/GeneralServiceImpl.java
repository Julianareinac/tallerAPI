package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeneralServiceImpl {
    private final UserRepository userRepository;

    public GeneralServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean validarUsuario(String usuario) {
        return userRepository.existsByLogin(usuario);
    }

    public boolean validateKey(String usuario, String key) {
        return userRepository.validateKey(usuario, key);
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public boolean validateEmail(String email) {
        return userRepository.validateEmail(email);
    }

    public User save(User user) {
        User result = userRepository.save(user);
        return result;
    }
}
