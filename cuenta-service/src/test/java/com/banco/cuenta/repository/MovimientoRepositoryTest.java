package com.banco.cuenta.repository;

import com.banco.cuenta.model.Cuenta;
import com.banco.cuenta.model.Movimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para validar el repositorio de movimientos con PostgreSQL
 * Estas pruebas verifican:
 * - Que las consultas específicas por fecha funcionen correctamente
 * - Que las relaciones entre movimientos y cuentas se mantengan
 * - Que el mapeo ORM sea correcto
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usar PostgreSQL real
public class MovimientoRepositoryTest {

    @Autowired
    private MovimientoRepository movimientoRepository;
    
    @Autowired
    private CuentaRepository cuentaRepository;
    
    private final String NUMERO_CUENTA = "MOV-TEST-123";
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaMedio;
    private LocalDateTime fechaFin;
    private Cuenta cuentaTest;

    @BeforeEach
    void setUp() {
        // Establecer fechas para pruebas
        fechaInicio = LocalDateTime.now().minusDays(30);
        fechaMedio = LocalDateTime.now().minusDays(15);
        fechaFin = LocalDateTime.now();
        
        // Limpiar datos previos
        List<Movimiento> movimientosExistentes = movimientoRepository.findByNumeroCuentaOrderByFechaDesc(NUMERO_CUENTA);
        movimientoRepository.deleteAll(movimientosExistentes);
        cuentaRepository.findById(NUMERO_CUENTA).ifPresent(cuenta -> cuentaRepository.delete(cuenta));
        
        // Crear cuenta de prueba
        cuentaTest = new Cuenta();
        cuentaTest.setNumeroCuenta(NUMERO_CUENTA);
        cuentaTest.setTipoCuenta("Ahorro");
        cuentaTest.setSaldoInicial(new BigDecimal("1000.00"));
        cuentaTest.setEstado(true);
        cuentaTest.setClienteId("CLI-MOV-TEST");
        
        cuentaRepository.save(cuentaTest);
        
        // Crear movimientos de prueba con diferentes fechas
        
        // Movimiento 1: Depósito hace 25 días
        crearMovimiento("DEPOSITO", new BigDecimal("500.00"), 
                new BigDecimal("1500.00"), fechaInicio.plusDays(5));
        
        // Movimiento 2: Retiro hace 20 días
        crearMovimiento("RETIRO", new BigDecimal("-200.00"), 
                new BigDecimal("1300.00"), fechaInicio.plusDays(10));
        
        // Movimiento 3: Depósito hace 10 días
        crearMovimiento("DEPOSITO", new BigDecimal("300.00"), 
                new BigDecimal("1600.00"), fechaMedio.plusDays(5));
        
        // Movimiento 4: Retiro hace 5 días
        crearMovimiento("RETIRO", new BigDecimal("-100.00"), 
                new BigDecimal("1500.00"), fechaFin.minusDays(5));
        
        // Movimiento 5: Depósito ayer
        crearMovimiento("DEPOSITO", new BigDecimal("200.00"), 
                new BigDecimal("1700.00"), fechaFin.minusDays(1));
    }
    
    private void crearMovimiento(String tipo, BigDecimal valor, BigDecimal saldo, LocalDateTime fecha) {
        Movimiento movimiento = new Movimiento();
        movimiento.setNumeroCuenta(NUMERO_CUENTA);
        movimiento.setTipoMovimiento(tipo);
        movimiento.setValor(valor);
        movimiento.setSaldo(saldo);
        movimiento.setFecha(fecha);
        
        movimientoRepository.save(movimiento);
    }

    @Test
    void testFindByNumeroCuentaOrderByFechaDesc() {
        // Verificar que podemos obtener todos los movimientos ordenados por fecha descendente
        List<Movimiento> movimientos = movimientoRepository.findByNumeroCuentaOrderByFechaDesc(NUMERO_CUENTA);
        
        // Debe haber 5 movimientos
        assertEquals(5, movimientos.size());
        
        // Verificar que están ordenados por fecha (el más reciente primero)
        LocalDateTime fechaAnterior = LocalDateTime.now().plusDays(1); // Una fecha futura para comenzar
        for (Movimiento movimiento : movimientos) {
            assertTrue(movimiento.getFecha().isBefore(fechaAnterior), 
                    "Los movimientos no están ordenados correctamente por fecha");
            fechaAnterior = movimiento.getFecha();
        }
        
        // Verificar el primer movimiento (el más reciente)
        Movimiento ultimoMovimiento = movimientos.get(0);
        assertEquals("DEPOSITO", ultimoMovimiento.getTipoMovimiento());
        assertEquals(0, new BigDecimal("200.00").compareTo(ultimoMovimiento.getValor()));
        assertEquals(0, new BigDecimal("1700.00").compareTo(ultimoMovimiento.getSaldo()));
    }
    
    @Test
    void testFindByNumeroCuentaAndFechaBetweenOrderByFechaDesc() {
        // Verificar la búsqueda por rango de fechas
        LocalDateTime rangoInicio = fechaMedio.minusDays(1); // Un día antes de fechaMedio
        LocalDateTime rangoFin = fechaFin; // Hasta hoy
        
        List<Movimiento> movimientosFiltrados = movimientoRepository
                .findByNumeroCuentaAndFechaBetweenOrderByFechaDesc(
                        NUMERO_CUENTA, rangoInicio, rangoFin);
        
        // Deben ser 3 movimientos (los más recientes)
        assertEquals(3, movimientosFiltrados.size());
        
        // Verificar que las fechas están dentro del rango
        for (Movimiento movimiento : movimientosFiltrados) {
            assertTrue(movimiento.getFecha().isEqual(rangoInicio) || 
                       movimiento.getFecha().isAfter(rangoInicio));
            assertTrue(movimiento.getFecha().isEqual(rangoFin) || 
                       movimiento.getFecha().isBefore(rangoFin));
        }
    }
    
    @Test
    void testDeleteMovimientosPorCuenta() {
        // Probar la eliminación de todos los movimientos de una cuenta
        List<Movimiento> movimientosAEliminar = movimientoRepository.findByNumeroCuentaOrderByFechaDesc(NUMERO_CUENTA);
        movimientoRepository.deleteAll(movimientosAEliminar);
        
        // Verificar que no hay movimientos para esa cuenta
        List<Movimiento> movimientos = movimientoRepository.findByNumeroCuentaOrderByFechaDesc(NUMERO_CUENTA);
        assertTrue(movimientos.isEmpty());
    }
    
    @Test
    void testConsistenciaSaldoMovimientos() {
        // Este test verifica que los saldos en los movimientos son consistentes
        // Es decir, el saldo de cada movimiento refleja correctamente el saldo después
        // de aplicar el movimiento

        List<Movimiento> movimientos = movimientoRepository
                .findByNumeroCuentaOrderByFechaDesc(NUMERO_CUENTA);

        // Invertir la lista para tener los movimientos en orden cronológico
        // (del más antiguo al más reciente)
        movimientos.sort(Comparator.comparing(Movimiento::getFecha));

        // Empezamos con el saldo inicial de la cuenta
        BigDecimal saldoEsperado = cuentaTest.getSaldoInicial();

        for (Movimiento movimiento : movimientos) {
            // Calculamos cuál debería ser el saldo DESPUÉS de este movimiento
            saldoEsperado = saldoEsperado.add(movimiento.getValor());

            // Comparamos el saldo calculado con el saldo registrado en el movimiento
            assertEquals(0, saldoEsperado.compareTo(movimiento.getSaldo()),
                    "El saldo del movimiento (" + movimiento.getSaldo() +
                    ") no coincide con el saldo esperado después de la operación (" + saldoEsperado +
                    ") para el movimiento con fecha " + movimiento.getFecha());
        }

        // Opcional: Verificar que el último saldo calculado coincide con el saldo actual de la cuenta
        // Esto requiere obtener el estado más reciente de la cuenta, lo cual puede ser redundante
        // si confiamos en que la lógica de actualización de saldo de la cuenta es correcta.
        // Cuenta cuentaActualizada = cuentaRepository.findById(NUMERO_CUENTA).orElseThrow();
        // assertEquals(0, saldoEsperado.compareTo(cuentaActualizada.getSaldoActual()));
    }
}
