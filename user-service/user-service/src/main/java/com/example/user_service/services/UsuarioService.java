package com.example.user_service.services;

import com.example.user_service.models.UsuarioModel;
import com.example.user_service.repositories.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioModel createUser(UsuarioModel user) {

        if (usuarioRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return usuarioRepository.save(user);
    }

    public UsuarioModel getUserById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public UsuarioModel updateUser(Long id, UsuarioModel updatedUser) {

        UsuarioModel existing = getUserById(id);

        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());

        return usuarioRepository.save(existing);
    }

    public void deleteUser(Long id) {
        usuarioRepository.deleteById(id);
    }

    public UsuarioModel patchUser(Long id, UsuarioModel partialUser) {

        UsuarioModel existing = getUserById(id);

        if (partialUser.getUsername() != null) {
            existing.setUsername(partialUser.getUsername());
        }

        if (partialUser.getEmail() != null) {

            if (usuarioRepository.findByEmail(partialUser.getEmail()).isPresent()) {
                throw new RuntimeException("El email ya está registrado");
            }

            existing.setEmail(partialUser.getEmail());
        }

        if (partialUser.getPassword() != null) {
            existing.setPassword(passwordEncoder.encode(partialUser.getPassword()));
        }

        return usuarioRepository.save(existing);
    }

    public List<UsuarioModel> getAllUsers(){
        return usuarioRepository.findAll();
    }

    // Pa autenticar
    public UsuarioModel authenticateUser(String email, String password) {

        UsuarioModel user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return user;
    }


}