package com.banco.cuenta.service;

import com.banco.cuenta.exception.SaldoNoDisponibleException;
import com.banco.cuenta.model.Cuenta;
import com.banco.cuenta.model.Movimiento;
import com.banco.cuenta.repository.CuentaRepository;
import com.banco.cuenta.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    public Movimiento registrarMovimiento(String numeroCuenta, String tipoMovimiento, BigDecimal valor) {
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (!cuenta.getEstado()) {
            throw new RuntimeException("La cuenta está inactiva");
        }

        BigDecimal saldoActual = cuenta.getSaldoInicial();
        BigDecimal nuevoSaldo;

        if (tipoMovimiento.equals("RETIRO")) {
            if (saldoActual.compareTo(valor) < 0) {
                throw new SaldoNoDisponibleException("Saldo no disponible");
            }
            nuevoSaldo = saldoActual.subtract(valor);
            valor = valor.negate();
        } else if (tipoMovimiento.equals("DEPOSITO")) {
            nuevoSaldo = saldoActual.add(valor);
        } else {
            throw new RuntimeException("Tipo de movimiento no válido");
        }

        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setValor(valor);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setNumeroCuenta(numeroCuenta);

        return movimientoRepository.save(movimiento);
    }

    public List<Movimiento> getMovimientosByNumeroCuenta(String numeroCuenta) {
        return movimientoRepository.findByNumeroCuentaOrderByFechaDesc(numeroCuenta);
    }

    public List<Movimiento> getMovimientosByFechas(String numeroCuenta, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.findByNumeroCuentaAndFechaBetweenOrderByFechaDesc(
            numeroCuenta, 
            fechaInicio, 
            fechaFin
        );
    }
}
