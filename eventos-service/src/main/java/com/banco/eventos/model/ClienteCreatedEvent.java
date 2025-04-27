package com.banco.eventos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCreatedEvent {
    private String clienteId;
    private String nombre;
    private String identificacion;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
}
