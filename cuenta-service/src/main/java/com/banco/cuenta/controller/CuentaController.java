package com.banco.cuenta.controller;

import com.banco.cuenta.dto.CuentaDTO;
import com.banco.cuenta.model.Cuenta;
import com.banco.cuenta.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping
    public ResponseEntity<Cuenta> createCuenta(@RequestBody CuentaDTO cuentaDTO) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(cuentaDTO.getNumeroCuenta());
        cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDTO.getSaldoInicial());
        cuenta.setEstado(cuentaDTO.getEstado());
        cuenta.setClienteId(cuentaDTO.getClienteId());

        Cuenta nuevaCuenta = cuentaService.createCuenta(cuenta);
        return new ResponseEntity<>(nuevaCuenta, HttpStatus.CREATED);
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<Cuenta> getCuentaByNumeroCuenta(@PathVariable String numeroCuenta) {
        return cuentaService.getCuentaByNumeroCuenta(numeroCuenta)
                .map(cuenta -> new ResponseEntity<>(cuenta, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Cuenta>> getCuentasByClienteId(@PathVariable String clienteId) {
        List<Cuenta> cuentas = cuentaService.getCuentasByClienteId(clienteId);
        return new ResponseEntity<>(cuentas, HttpStatus.OK);
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<Cuenta> updateCuenta(@PathVariable String numeroCuenta, @RequestBody CuentaDTO cuentaDTO) {
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDTO.getSaldoInicial());
        cuenta.setEstado(cuentaDTO.getEstado());

        Cuenta cuentaActualizada = cuentaService.updateCuenta(numeroCuenta, cuenta);
        return new ResponseEntity<>(cuentaActualizada, HttpStatus.OK);
    }
}
