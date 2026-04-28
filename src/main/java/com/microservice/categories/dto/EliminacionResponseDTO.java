package com.microservice.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EliminacionResponseDTO {

    private String mensaje;
    private boolean eliminadaFisicamente;
    private boolean requiereReasignacion;
    private Long nuevaCategoriaId;
}
