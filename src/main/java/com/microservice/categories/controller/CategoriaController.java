package com.microservice.categories.controller;

import com.microservice.categories.dto.CategoriaRequestDTO;
import com.microservice.categories.dto.CategoriaResponseDTO;
import com.microservice.categories.dto.EliminacionResponseDTO;
import com.microservice.categories.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    // GET /api/categories
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> obtenerTodasCategorias() {
        return ResponseEntity.ok(categoriaService.obtenerTodasCategorias());
    }

    // POST /api/v1/categorias
    // Crear una nueva categoria
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> crearCategoria(
            @Valid @RequestBody CategoriaRequestDTO dto) {

        CategoriaResponseDTO respuesta = categoriaService.crearCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // PUT /api/v1/categorias/{id}
    // Editar nombre o descripcion de una categoria
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> editarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO dto) {

        CategoriaResponseDTO respuesta = categoriaService.editarCategoria(id, dto);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EliminacionResponseDTO> eliminarCategoria(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestParam(defaultValue = "false") boolean tieneGastos,
            @RequestParam(required = false) Long nuevaCategoriaId) {

        EliminacionResponseDTO respuesta = categoriaService
                .eliminarCategoria(id, usuarioId, tieneGastos, nuevaCategoriaId);

        HttpStatus status = respuesta.isEliminadaFisicamente()
                ? HttpStatus.OK
                : HttpStatus.ACCEPTED;
        return ResponseEntity.status(status).body(respuesta);
    }

    // GET /api/v1/categorias/usuario/{usuarioId}
    // Listar todas las categorias activas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CategoriaResponseDTO>> listarCategorias(
            @PathVariable Long usuarioId) {

        List<CategoriaResponseDTO> categorias = categoriaService.listarCategorias(usuarioId);
        return ResponseEntity.ok(categorias);
    }

    // GET /api/v1/categorias/{id}?usuarioId=
    // Obtener una categoria especifica
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> obtenerCategoria(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {

        CategoriaResponseDTO categoria = categoriaService.obtenerCategoria(id, usuarioId);
        return ResponseEntity.ok(categoria);
    }
}
