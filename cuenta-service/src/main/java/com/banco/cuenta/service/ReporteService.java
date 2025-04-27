package com.banco.cuenta.service;

import com.banco.cuenta.dto.CuentaReporteDTO;
import com.banco.cuenta.dto.MovimientoReporteDTO;
import com.banco.cuenta.dto.ReporteEstadoCuentaDTO;
import com.banco.cuenta.model.Cuenta;
import com.banco.cuenta.model.Movimiento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final CuentaService cuentaService;
    private final MovimientoService movimientoService;
    
    public ReporteEstadoCuentaDTO generarReporteEstadoCuenta(String clienteId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.info("Generando reporte para cliente {} desde {} hasta {}", clienteId, fechaInicio, fechaFin);
        
        // Obtener todas las cuentas del cliente
        List<Cuenta> cuentas = cuentaService.getCuentasByClienteId(clienteId);
        if (cuentas.isEmpty()) {
            log.warn("No se encontraron cuentas para el cliente {}", clienteId);
            return ReporteEstadoCuentaDTO.builder()
                    .clienteId(clienteId)
                    .fechaInicio(fechaInicio)
                    .fechaFin(fechaFin)
                    .fechaGeneracion(LocalDateTime.now())
                    .totalCuentas(0)
                    .saldoTotalInicial(BigDecimal.ZERO)
                    .saldoTotalFinal(BigDecimal.ZERO)
                    .totalDebitos(BigDecimal.ZERO)
                    .totalCreditos(BigDecimal.ZERO)
                    .cuentas(new ArrayList<>())
                    .build();
        }
        
        // Variables para calcular los totales del reporte
        BigDecimal saldoTotalInicial = BigDecimal.ZERO;
        BigDecimal saldoTotalFinal = BigDecimal.ZERO;
        BigDecimal totalDebitos = BigDecimal.ZERO;
        BigDecimal totalCreditos = BigDecimal.ZERO;
        
        // Procesar cada cuenta
        List<CuentaReporteDTO> cuentasReporte = new ArrayList<>();
        
        for (Cuenta cuenta : cuentas) {
            // Obtener movimientos en el rango de fechas
            List<Movimiento> movimientos = movimientoService.getMovimientosByFechas(
                    cuenta.getNumeroCuenta(), fechaInicio, fechaFin);
            
            if (movimientos.isEmpty()) {
                // Si no hay movimientos, usar el saldo inicial de la cuenta
                CuentaReporteDTO cuentaDTO = CuentaReporteDTO.builder()
                        .numeroCuenta(cuenta.getNumeroCuenta())
                        .tipoCuenta(cuenta.getTipoCuenta())
                        .estado(cuenta.getEstado())
                        .saldoInicial(cuenta.getSaldoInicial())
                        .saldoFinal(cuenta.getSaldoInicial())
                        .totalDebitos(BigDecimal.ZERO)
                        .totalCreditos(BigDecimal.ZERO)
                        .movimientos(new ArrayList<>())
                        .build();
                
                cuentasReporte.add(cuentaDTO);
                saldoTotalInicial = saldoTotalInicial.add(cuenta.getSaldoInicial());
                saldoTotalFinal = saldoTotalFinal.add(cuenta.getSaldoInicial());
                continue;
            }
            
            // Ordenar movimientos por fecha
            movimientos.sort(Comparator.comparing(Movimiento::getFecha));
            
            // Calcular saldo inicial - es el saldo antes del primer movimiento
            BigDecimal saldoInicial;
            if (movimientos.get(0).getTipoMovimiento().equals("DEPOSITO")) {
                saldoInicial = movimientos.get(0).getSaldo().subtract(movimientos.get(0).getValor());
            } else {
                saldoInicial = movimientos.get(0).getSaldo().add(movimientos.get(0).getValor());
            }
            
            // Calcular saldo final - es el saldo después del último movimiento
            BigDecimal saldoFinal = movimientos.get(movimientos.size() - 1).getSaldo();
            
            // Calcular débitos y créditos
            BigDecimal debitosCuenta = BigDecimal.ZERO;
            BigDecimal creditosCuenta = BigDecimal.ZERO;
            
            // Mapear movimientos a DTOs
            List<MovimientoReporteDTO> movimientosDTO = new ArrayList<>();
            
            for (Movimiento mov : movimientos) {
                if (mov.getTipoMovimiento().equals("RETIRO")) {
                    debitosCuenta = debitosCuenta.add(mov.getValor());
                } else {
                    creditosCuenta = creditosCuenta.add(mov.getValor());
                }
                
                MovimientoReporteDTO movDTO = MovimientoReporteDTO.builder()
                        .id(mov.getId())
                        .fecha(mov.getFecha())
                        .tipoMovimiento(mov.getTipoMovimiento())
                        .valor(mov.getValor())
                        .saldoResultante(mov.getSaldo())
                        .descripcion(generarDescripcion(mov))
                        .build();
                
                movimientosDTO.add(movDTO);
            }
            
            // Crear DTO de la cuenta
            CuentaReporteDTO cuentaDTO = CuentaReporteDTO.builder()
                    .numeroCuenta(cuenta.getNumeroCuenta())
                    .tipoCuenta(cuenta.getTipoCuenta())
                    .estado(cuenta.getEstado())
                    .saldoInicial(saldoInicial)
                    .saldoFinal(saldoFinal)
                    .totalDebitos(debitosCuenta)
                    .totalCreditos(creditosCuenta)
                    .movimientos(movimientosDTO)
                    .build();
            
            cuentasReporte.add(cuentaDTO);
            
            // Actualizar totales
            saldoTotalInicial = saldoTotalInicial.add(saldoInicial);
            saldoTotalFinal = saldoTotalFinal.add(saldoFinal);
            totalDebitos = totalDebitos.add(debitosCuenta);
            totalCreditos = totalCreditos.add(creditosCuenta);
        }
        
        // Crear el reporte final
        return ReporteEstadoCuentaDTO.builder()
                .clienteId(clienteId)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .fechaGeneracion(LocalDateTime.now())
                .totalCuentas(cuentas.size())
                .saldoTotalInicial(saldoTotalInicial)
                .saldoTotalFinal(saldoTotalFinal)
                .totalDebitos(totalDebitos)
                .totalCreditos(totalCreditos)
                .cuentas(cuentasReporte)
                .build();
    }
    
    private String generarDescripcion(Movimiento movimiento) {
        String tipo = movimiento.getTipoMovimiento().equals("DEPOSITO") ? "Depósito a" : "Retiro de";
        return tipo + " cuenta " + movimiento.getNumeroCuenta();
    }
}
