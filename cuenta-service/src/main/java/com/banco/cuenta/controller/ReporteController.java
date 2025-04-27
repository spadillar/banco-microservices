package com.banco.cuenta.controller;

import com.banco.cuenta.dto.ReporteEstadoCuentaDTO;
import com.banco.cuenta.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/estado-cuenta")
    public ResponseEntity<ReporteEstadoCuentaDTO> getReporteEstadoCuenta(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        ReporteEstadoCuentaDTO reporte = reporteService.generarReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);
        return new ResponseEntity<>(reporte, HttpStatus.OK);
    }
}
