package com.microservice.categories.service.impl;

import com.microservice.categories.dto.CategoriaRequestDTO;
import com.microservice.categories.dto.CategoriaResponseDTO;
import com.microservice.categories.dto.EliminacionResponseDTO;
import com.microservice.categories.exception.NombreDuplicadoException;
import com.microservice.categories.exception.ResourceNotFoundException;
import com.microservice.categories.model.Categoria;
import com.microservice.categories.repository.CategoriaRepository;
import com.microservice.categories.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacion de la logica de negocio para el manejo de categorias.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // ----------------------------------------------------------
    //  Crear Categoria
    // ----------------------------------------------------------

    @Override
    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto) {
        log.info("Creando categoria '{}' para usuario {}", dto.getNombre(), dto.getUsuarioId());

        validarNombreUnico(dto.getUsuarioId(), dto.getNombre(), null);

        Categoria categoria = Categoria.builder()
                .usuarioId(dto.getUsuarioId())
                .nombre(dto.getNombre().trim())
                .descripcion(dto.getDescripcion())
                .activa(true)
                .build();

        categoria = categoriaRepository.save(categoria);
        log.info("Categoria creada con ID {}", categoria.getId());
        return mapToResponseDTO(categoria);
    }

    // ----------------------------------------------------------
    //  Editar Categoria
    // ----------------------------------------------------------

    @Override
    @Transactional
    public CategoriaResponseDTO editarCategoria(Long id, CategoriaRequestDTO dto) {
        log.info("Editando categoria {} del usuario {}", id, dto.getUsuarioId());

        Categoria categoria = obtenerActivaOLanzarError(id, dto.getUsuarioId());

        // Solo valida unicidad de nombre si realmente cambia
        boolean nombreCambia = !categoria.getNombre()
                .equalsIgnoreCase(dto.getNombre().trim());

        if (nombreCambia) {
            validarNombreUnico(dto.getUsuarioId(), dto.getNombre(), id);
        }

        categoria.setNombre(dto.getNombre().trim());
        categoria.setDescripcion(dto.getDescripcion());

        categoria = categoriaRepository.save(categoria);

        // Punto de extension: si el nombre cambio, aqui se publicaria un evento
        // hacia Transacciones para actualizar el nombre en los gastos asociados.
        // Ejemplo: eventPublisher.publish(new CategoriaRenombradaEvent(id, nuevoNombre));
        if (nombreCambia) {
            log.info("Nombre de categoria {} cambio. Transacciones debe ser notificado.", id);
        }

        return mapToResponseDTO(categoria);
    }

    // ----------------------------------------------------------
    //  Eliminar Categoria
    // ----------------------------------------------------------

    @Override
    @Transactional
    public EliminacionResponseDTO eliminarCategoria(
            Long id, Long usuarioId, boolean tieneGastos, Long nuevaCategoriaId) {

        log.info("Eliminando categoria {} del usuario {}. tieneGastos={}, nuevaCategoriaId={}",
                id, usuarioId, tieneGastos, nuevaCategoriaId);

        Categoria categoria = obtenerActivaOLanzarError(id, usuarioId);

        // Si hay una nueva categoria para reasignar, validar que exista y sea del mismo usuario
        if (nuevaCategoriaId != null) {
            categoriaRepository.findByIdAndUsuarioIdAndActivaTrue(nuevaCategoriaId, usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "La categoria de reasignacion no existe o no pertenece al usuario: "
                                    + nuevaCategoriaId));
        }

        if (!tieneGastos) {
            // Sin gastos asociados: eliminacion fisica directa
            categoriaRepository.delete(categoria);
            log.info("Categoria {} eliminada fisicamente.", id);

            return EliminacionResponseDTO.builder()
                    .mensaje("Categoria eliminada correctamente.")
                    .eliminadaFisicamente(true)
                    .requiereReasignacion(false)
                    .build();
        }

        // Con gastos asociados: borrado logico
        categoria.setActiva(false);
        categoriaRepository.save(categoria);
        log.info("Categoria {} desactivada (borrado logico). Gastos asociados presentes.", id);

        String mensaje;
        if (nuevaCategoriaId != null) {
            // Punto de extension: notificar a Transacciones que debe reasignar
            // todos los gastos de categoriaId -> nuevaCategoriaId.
            // Ejemplo: eventPublisher.publish(new ReasignacionGastosEvent(id, nuevaCategoriaId));
            log.info("Reasignacion requerida: gastos de categoria {} -> {}", id, nuevaCategoriaId);
            mensaje = "Categoria desactivada. Los gastos asociados deben reasignarse "
                    + "a la categoria " + nuevaCategoriaId + " en el servicio de Transacciones.";
        } else {
            mensaje = "Categoria desactivada. Tiene gastos asociados. "
                    + "Provee nuevaCategoriaId para reasignarlos en Transacciones.";
        }

        return EliminacionResponseDTO.builder()
                .mensaje(mensaje)
                .eliminadaFisicamente(false)
                .requiereReasignacion(true)
                .nuevaCategoriaId(nuevaCategoriaId)
                .build();
    }

    // ----------------------------------------------------------
    //  Listar Categorias
    // ----------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarCategorias(Long usuarioId) {
        log.info("Listando categorias del usuario {}", usuarioId);
        return categoriaRepository.findByUsuarioIdAndActivaTrue(usuarioId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------
    //  Obtener una Categoria
    // ----------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerCategoria(Long id, Long usuarioId) {
        log.info("Obteniendo categoria {} del usuario {}", id, usuarioId);
        Categoria categoria = obtenerActivaOLanzarError(id, usuarioId);
        return mapToResponseDTO(categoria);
    }

    // ----------------------------------------------------------
    //  Metodos privados de apoyo
    // ----------------------------------------------------------

    /**
     * Valida que no exista otra categoria activa con el mismo nombre para el usuario.
     * Si se pasa un ID se excluye ese registro (util en edicion).
     */
    private void validarNombreUnico(Long usuarioId, String nombre, Long idExcluir) {
        boolean existe;
        if (idExcluir == null) {
            existe = categoriaRepository
                    .existsByUsuarioIdAndNombreIgnoreCaseAndActivaTrue(usuarioId, nombre.trim());
        } else {
            existe = categoriaRepository
                    .existsByUsuarioIdAndNombreIgnoreCaseAndActivaTrueAndIdNot(
                            usuarioId, nombre.trim(), idExcluir);
        }

        if (existe) {
            throw new NombreDuplicadoException(
                    "Ya existe una categoria con el nombre '" + nombre
                            + "' para el usuario " + usuarioId);
        }
    }

    /**
     * Busca una categoria activa por ID y usuario. Lanza excepcion si no existe.
     */
    private Categoria obtenerActivaOLanzarError(Long id, Long usuarioId) {
        return categoriaRepository
                .findByIdAndUsuarioIdAndActivaTrue(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoria no encontrada con ID " + id
                                + " para el usuario " + usuarioId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerTodasCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapea la entidad al DTO de respuesta.
     */
    private CategoriaResponseDTO mapToResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .id(categoria.getId())
                .usuarioId(categoria.getUsuarioId())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .activa(categoria.getActiva())
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaActualizacion(categoria.getFechaActualizacion())
                .build();
    }
}
