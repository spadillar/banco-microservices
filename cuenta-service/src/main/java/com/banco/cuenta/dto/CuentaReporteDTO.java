package com.banco.cuenta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaReporteDTO {
    // Información de la cuenta
    private String numeroCuenta;
    private String tipoCuenta;
    private Boolean estado;
    
    // Saldos
    private BigDecimal saldoInicial;
    private BigDecimal saldoFinal;
    private BigDecimal totalDebitos;
    private BigDecimal totalCreditos;
    
    // Movimientos
    private List<MovimientoReporteDTO> movimientos;
}
