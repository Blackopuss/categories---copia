package com.example.user_service.controllers;

import com.example.user_service.models.LoginRequest;
import com.example.user_service.models.UserResponse;
import com.example.user_service.models.UsuarioModel;
import com.example.user_service.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioModel> createUser(
            @Valid @RequestBody UsuarioModel user) {

        return new ResponseEntity<>(
                usuarioService.createUser(user),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<java.util.List<UsuarioModel>> getAllUsers() {
        return ResponseEntity.ok(usuarioService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioModel user) {

        return ResponseEntity.ok(usuarioService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usuarioService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioModel> patchUser(
            @PathVariable Long id,
            @RequestBody UsuarioModel user) {

        return ResponseEntity.ok(usuarioService.patchUser(id, user));
    }

    // endpoint para Autenticacion
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request){

        UsuarioModel user = usuarioService.authenticateUser(
                request.getEmail(),
                request.getPassword()
        );

        UserResponse response = new UserResponse(
                user.getId(), user.getUsername(), user.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}