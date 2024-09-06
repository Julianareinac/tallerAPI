package com.example.demo.controllers;

import com.example.demo.model.AdminUserDTO;
import com.example.demo.model.User;
import com.example.demo.responses.ErrorResponse;
import com.example.demo.service.GeneralServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final GeneralServiceImpl generalService;
    private List<User> users = new ArrayList<>();

    public UserController(GeneralServiceImpl generalService) {
        this.generalService = generalService;
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody User adminUserDTO) {
        if (generalService.validarUsuario(adminUserDTO.getLogin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(409, "El usuario ya existe"));
        }
        if (generalService.validateEmail(adminUserDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(400, "El email ya existe"));
        }
        User result = generalService.save(adminUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody AdminUserDTO adminUserDTO) {
        Optional<User> existingUser = users.stream()
                .filter(user -> user.getLogin().equals(adminUserDTO.getLogin()))
                .findFirst();

        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado"));
        }

        // Actualizar la información del usuario
        User user = existingUser.get();
        user.setFirstName(adminUserDTO.getFirstName());
        user.setLastName(adminUserDTO.getLastName());
        user.setEmail(adminUserDTO.getEmail());
        user.setActivated(adminUserDTO.isActivated());
        user.setLangKey(adminUserDTO.getLangKey());

        return ResponseEntity.ok(user);
    }

    @PatchMapping
    public ResponseEntity<?> patchUser(@RequestBody AdminUserDTO adminUserDTO) {
        Optional<User> existingUser = users.stream()
                .filter(user -> user.getLogin().equals(adminUserDTO.getLogin()))
                .findFirst();

        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado"));
        }

        // Actualizar parcialmente la información del usuario
        User user = existingUser.get();
        if (adminUserDTO.getFirstName() != null) user.setFirstName(adminUserDTO.getFirstName());
        if (adminUserDTO.getLastName() != null) user.setLastName(adminUserDTO.getLastName());
        if (adminUserDTO.getEmail() != null) user.setEmail(adminUserDTO.getEmail());
        if (adminUserDTO.getLangKey() != null) user.setLangKey(adminUserDTO.getLangKey());

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<AdminUserDTO>> getAllUsers(@RequestParam Optional<Integer> page,
                                                          @RequestParam Optional<Integer> size,
                                                          @RequestParam Optional<String> sort) {
        // Implementar lógica de paginación y ordenamiento según los parámetros
        List<AdminUserDTO> dtoList = users.stream()
                .map(AdminUserDTO::new)
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{login}")
    public ResponseEntity<?> getUser(@PathVariable String login) {
        Optional<User> user = users.stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst();

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado"));
        }

        return ResponseEntity.ok(new AdminUserDTO(user.get()));
    }

    @DeleteMapping("/{login}")
    public ResponseEntity<?> deleteUser(@PathVariable String login) {
        Optional<User> user = users.stream()
                .filter(u -> u.getLogin().equals(login))
                .findFirst();

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado"));
        }

        users.remove(user.get());

        return ResponseEntity.noContent().build();
    }
}
