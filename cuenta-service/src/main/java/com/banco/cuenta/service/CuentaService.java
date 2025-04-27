package com.banco.cuenta.service;

import com.banco.cuenta.model.Cuenta;
import com.banco.cuenta.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public Cuenta createCuenta(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    public Optional<Cuenta> getCuentaByNumeroCuenta(String numeroCuenta) {
        return cuentaRepository.findById(numeroCuenta);
    }

    public List<Cuenta> getCuentasByClienteId(String clienteId) {
        return cuentaRepository.findByClienteId(clienteId);
    }

    public List<Cuenta> getAllCuentas() {
        return cuentaRepository.findAll();
    }

    public Cuenta updateCuenta(String numeroCuenta, Cuenta cuentaDetails) {
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        cuenta.setTipoCuenta(cuentaDetails.getTipoCuenta());
        cuenta.setSaldoInicial(cuentaDetails.getSaldoInicial());
        cuenta.setEstado(cuentaDetails.getEstado());

        return cuentaRepository.save(cuenta);
    }
}
