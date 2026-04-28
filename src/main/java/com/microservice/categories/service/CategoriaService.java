package com.microservice.categories.service;

import com.microservice.categories.dto.CategoriaRequestDTO;
import com.microservice.categories.dto.CategoriaResponseDTO;
import com.microservice.categories.dto.EliminacionResponseDTO;

import java.util.List;

/**
 * Contrato del servicio de Categorias.
 */
public interface CategoriaService {

    /**
     * Crea una nueva categoria validando que el nombre sea unico por usuario.
     */
    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO dto);

    /**
     * Edita el nombre o descripcion de una categoria.
     * El cambio de nombre debe propagarse a Transacciones
     * (referencia logica: el cliente o un evento notifica al otro servicio).
     */
    CategoriaResponseDTO editarCategoria(Long id, CategoriaRequestDTO dto);

    /**
     * Elimina una categoria.
     *
     * <p>Flujo:</p>
     * <ol>
     *   <li>Si la categoria no tiene gastos asociados (determinado por el parametro
     *       {@code tieneGastos}), se elimina fisicamente.</li>
     *   <li>Si tiene gastos y se provee {@code nuevaCategoriaId}, se desactiva
     *       y se retorna la nueva categoria para que el cliente notifique a
     *       Transacciones.</li>
     *   <li>Si tiene gastos y no se provee nueva categoria, se desactiva
     *       (borrado logico) y se advierte al cliente.</li>
     * </ol>
     *
     * @param id              ID de la categoria a eliminar
     * @param usuarioId       ID del usuario propietario
     * @param tieneGastos     indica si Transacciones reporto gastos asociados
     * @param nuevaCategoriaId categoria a la que reasignar (puede ser null)
     */
    EliminacionResponseDTO eliminarCategoria(
            Long id, Long usuarioId, boolean tieneGastos, Long nuevaCategoriaId);

    /**
     * Lista todas las categorias activas de un usuario.
     */
    List<CategoriaResponseDTO> listarCategorias(Long usuarioId);

    /**
     * Lista todas las categorias de todos los usuarios (para pruebas/root).
     */
    List<CategoriaResponseDTO> obtenerTodasCategorias();

    /**
     * Obtiene una categoria especifica de un usuario.
     */
    CategoriaResponseDTO obtenerCategoria(Long id, Long usuarioId);
}
