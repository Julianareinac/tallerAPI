package com.example.demo.controllers;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.responses.AuthResponse;
import com.example.demo.responses.ErrorResponse;
import com.example.demo.service.GeneralServiceImpl;
import com.example.demo.utils.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GeneralServiceImpl generalService;

    public AuthController(GeneralServiceImpl generalService) {
        this.generalService = generalService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // Lógica para validar credenciales
        if(!generalService.validarUsuario(loginRequest.getLogin())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(400, "El usuario no existe"));
        }
        if(!generalService.validateKey(loginRequest.getLogin(), loginRequest.getPassword())){
            System.out.println("Credenciales inválidas para: " + loginRequest.getLogin() + " con contraseña " + loginRequest.getPassword());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Credenciales inválidas"));
        }
        String token = JwtUtil.generarToken(loginRequest.getLogin());
        return new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK);
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<?> recoverPassword(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody User dto) {

        Optional<User> user = generalService.findByLogin(dto.getLogin());

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(404, "Usuario no encontrado"));
        }

        // Verificar que el encabezado de autorización esté presente y tenga el formato correcto
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Token no proporcionado o formato incorrecto"));
        }

        String token = authorizationHeader.substring(7);

        try {
            // Validar el token
            JwtUtil.validateToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Token inválido o expirado"));
        }

        // Verificar la respuesta de seguridad o código temporal
        boolean isVerificationValid = generalService.verifySecurityAnswer(dto.getLogin(), dto.getSecurityAnswer());
        if (!isVerificationValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Respuesta de verificación incorrecta"));
        }


        generalService.updatePassword(user.get(),dto.getPassword());



        return ResponseEntity.ok(user);
    }


}
