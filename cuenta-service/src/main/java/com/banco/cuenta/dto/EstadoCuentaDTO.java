package com.banco.cuenta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoCuentaDTO {
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldoDisponible;
    private String clienteId;
    private LocalDateTime fecha;
    private String tipoMovimiento;
    private BigDecimal valor;
    private Boolean estado;
}
