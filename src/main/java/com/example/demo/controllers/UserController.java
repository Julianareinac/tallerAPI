package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.model.UserDTO;
import com.example.demo.responses.ErrorResponse;
import com.example.demo.service.GeneralServiceImpl;
import com.example.demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
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
    private final JwtUtil jwtUtil;

    public UserController(GeneralServiceImpl generalService, JwtUtil jwtUtil) {
        this.generalService = generalService;
        this.jwtUtil = jwtUtil;
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

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User adminUserDTO, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        Optional<User> existingUser = generalService.findByLogin(adminUserDTO.getLogin());

        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado"));
        }
        if (adminUserDTO.getLogin() == null || adminUserDTO.getLogin().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "El campo 'login' es obligatorio"));
        }
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Token no proporcionado o formato incorrecto"));
        }

        String token = authorizationHeader.substring(7).trim();

        try {
            JwtUtil.validateToken(token);
        } catch (Exception e) {
            System.out.println("Error al validar el token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Token inválido o expirado"));
        }

        // Actualizar la información del usuario
        User user = existingUser.get();
        user.setFirstName(adminUserDTO.getFirstName());
        user.setLastName(adminUserDTO.getLastName());
        user.setEmail(adminUserDTO.getEmail());
        user.setActivated(adminUserDTO.isActivated());
        user.setLangKey(adminUserDTO.getLangKey());

        generalService.save(user);

        return ResponseEntity.ok(user);
    }

    @PatchMapping
    public ResponseEntity<?> patchUser(@RequestBody UserDTO adminUserDTO) {
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
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam Optional<Integer> page,
                                                          @RequestParam Optional<Integer> size,
                                                          @RequestParam Optional<String> sort) {

        int pageNumber = page.orElse(0); // Página por defecto: 0
        int pageSize = size.orElse(10);  // Tamaño por defecto: 10
        String sortBy = sort.orElse("id"); // Ordenar por defecto por "id"


        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());


        Page<User> userPage = generalService.getAllUsers(pageable);

        List<UserDTO> dtoList = userPage.stream()
                .map(UserDTO::new)
                .toList();

        return ResponseEntity.ok(dtoList);
    }


    @GetMapping("/{login}")
    public ResponseEntity<?> getUser(@PathVariable String login) {
        Optional<User> user = generalService.findByLogin(login);
        if (login == null || login.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "El parámetro 'login' es obligatorio y no puede estar vacío"));
        }

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado"));
        }

        return ResponseEntity.ok(new UserDTO(user.get()));
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
