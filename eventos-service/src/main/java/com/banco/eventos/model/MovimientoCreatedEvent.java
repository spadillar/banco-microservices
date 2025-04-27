package com.banco.eventos.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCreatedEvent {
    private Long movimientoId;
    private String numeroCuenta;
    private String clienteId;
    private String tipoMovimiento;
    private BigDecimal valor;
    private BigDecimal saldoResultante;
    private LocalDateTime fecha;
}
