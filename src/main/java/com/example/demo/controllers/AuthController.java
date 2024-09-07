package com.example.demo.controllers;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.responses.AuthResponse;
import com.example.demo.responses.ErrorResponse;
import com.example.demo.service.GeneralServiceImpl;
import com.example.demo.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    @PostMapping("/recuperar-contrasena")
//    public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordRequest recoverPasswordRequest) {
//        // Lógica para recuperar contraseña
//        if (emailExists(recoverPasswordRequest.getEmail())) {
//            sendRecoveryInstructions(recoverPasswordRequest.getEmail());
//            return ResponseEntity.ok(new MessageResponse("Instrucciones enviadas"));
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new ErrorResponse(404, "Correo no encontrado"));
//        }
//    }

    private void sendRecoveryInstructions(String email) {
        // Aquí va la lógica para enviar las instrucciones de recuperación
    }
}
