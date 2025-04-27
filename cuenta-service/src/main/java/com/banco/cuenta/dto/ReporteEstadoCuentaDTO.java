package com.banco.cuenta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteEstadoCuentaDTO {
    // Información del cliente
    private String clienteId;
    private String nombreCliente;
    
    // Periodo del reporte
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaGeneracion;
    
    // Resumen
    private int totalCuentas;
    private BigDecimal saldoTotalInicial;
    private BigDecimal saldoTotalFinal;
    private BigDecimal totalDebitos;
    private BigDecimal totalCreditos;
    
    // Detalle por cuenta
    private List<CuentaReporteDTO> cuentas;
}
