package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public void updatePassword(User user, String password) {
        user.setPassword(password);
    }

    public boolean verifySecurityAnswer(String username, String securityAnswer) {
        Optional<User> userOptional = userRepository.findByLogin(username);

        if (userOptional.isEmpty()) {
            return false; // Usuario no encontrado
        }

        User user = userOptional.get();
        return user.getSecurityAnswer().equalsIgnoreCase(securityAnswer);
    }
}
