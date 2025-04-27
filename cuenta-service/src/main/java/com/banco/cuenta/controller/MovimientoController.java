package com.banco.cuenta.controller;

import com.banco.cuenta.dto.EstadoCuentaDTO;
import com.banco.cuenta.dto.MovimientoDTO;
import com.banco.cuenta.model.Cuenta;
import com.banco.cuenta.model.Movimiento;
import com.banco.cuenta.service.CuentaService;
import com.banco.cuenta.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<MovimientoDTO> registrarMovimiento(@RequestBody MovimientoDTO movimientoDTO) {
        Movimiento movimiento = movimientoService.registrarMovimiento(
            movimientoDTO.getNumeroCuenta(),
            movimientoDTO.getTipoMovimiento(),
            movimientoDTO.getValor()
        );

        MovimientoDTO response = new MovimientoDTO();
        response.setId(movimiento.getId());
        response.setFecha(movimiento.getFecha());
        response.setTipoMovimiento(movimiento.getTipoMovimiento());
        response.setValor(movimiento.getValor());
        response.setSaldo(movimiento.getSaldo());
        response.setNumeroCuenta(movimiento.getNumeroCuenta());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/reportes")
    public ResponseEntity<List<EstadoCuentaDTO>> getEstadoCuenta(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<Cuenta> cuentas = cuentaService.getCuentasByClienteId(clienteId);
        
        List<EstadoCuentaDTO> estadoCuentas = cuentas.stream()
            .flatMap(cuenta -> {
                List<Movimiento> movimientos = movimientoService.getMovimientosByFechas(
                    cuenta.getNumeroCuenta(), 
                    fechaInicio, 
                    fechaFin
                );
                
                return movimientos.stream().map(mov -> {
                    EstadoCuentaDTO dto = new EstadoCuentaDTO();
                    dto.setNumeroCuenta(cuenta.getNumeroCuenta());
                    dto.setTipoCuenta(cuenta.getTipoCuenta());
                    dto.setSaldoDisponible(mov.getSaldo());
                    dto.setClienteId(cuenta.getClienteId());
                    dto.setFecha(mov.getFecha());
                    dto.setTipoMovimiento(mov.getTipoMovimiento());
                    dto.setValor(mov.getValor());
                    dto.setEstado(cuenta.getEstado());
                    return dto;
                });
            })
            .collect(Collectors.toList());

        return new ResponseEntity<>(estadoCuentas, HttpStatus.OK);
    }
}
