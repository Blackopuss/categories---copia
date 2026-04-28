package com.microservice.categories.repository;

import com.microservice.categories.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioIdAndActivaTrue(Long usuarioId);

    List<Categoria> findByUsuarioId(Long usuarioId);

    Optional<Categoria> findByIdAndUsuarioIdAndActivaTrue(Long id, Long usuarioId);

    Optional<Categoria> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndNombreIgnoreCaseAndActivaTrue(Long usuarioId, String nombre);

    boolean existsByUsuarioIdAndNombreIgnoreCaseAndActivaTrueAndIdNot(
            Long usuarioId, String nombre, Long id);
}
