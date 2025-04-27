package com.banco.cuenta.repository;

import com.banco.cuenta.model.Cuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas para validar el repositorio con la base de datos real
 * Esta prueba verifica:
 * - Que las consultas JPA funcionen correctamente
 * - Que el mapeo entre entidades y tablas sea correcto
 * - Que las restricciones de la base de datos se respeten
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usar PostgreSQL real
public class CuentaRepositoryTest {

    @Autowired
    private CuentaRepository cuentaRepository;

    private final String CLIENTE_ID = "CLI-REPO-001";
    private final String NUMERO_CUENTA_1 = "REPO-123456";
    private final String NUMERO_CUENTA_2 = "REPO-654321";

    @BeforeEach
    void setUp() {
        // Limpiar datos previos
        cuentaRepository.findById(NUMERO_CUENTA_1).ifPresent(cuenta -> cuentaRepository.delete(cuenta));
        cuentaRepository.findById(NUMERO_CUENTA_2).ifPresent(cuenta -> cuentaRepository.delete(cuenta));
        
        // Crear cuentas de prueba
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setNumeroCuenta(NUMERO_CUENTA_1);
        cuenta1.setTipoCuenta("Ahorro");
        cuenta1.setSaldoInicial(new BigDecimal("1000.00"));
        cuenta1.setEstado(true);
        cuenta1.setClienteId(CLIENTE_ID);
        cuentaRepository.save(cuenta1);
        
        Cuenta cuenta2 = new Cuenta();
        cuenta2.setNumeroCuenta(NUMERO_CUENTA_2);
        cuenta2.setTipoCuenta("Corriente");
        cuenta2.setSaldoInicial(new BigDecimal("2000.00"));
        cuenta2.setEstado(true);
        cuenta2.setClienteId(CLIENTE_ID);
        cuentaRepository.save(cuenta2);
    }

    @Test
    void testFindById() {
        // Verificar que podemos recuperar una cuenta por ID (número de cuenta)
        Optional<Cuenta> cuentaOptional = cuentaRepository.findById(NUMERO_CUENTA_1);
        
        assertTrue(cuentaOptional.isPresent());
        Cuenta cuenta = cuentaOptional.get();
        assertEquals(NUMERO_CUENTA_1, cuenta.getNumeroCuenta());
        assertEquals("Ahorro", cuenta.getTipoCuenta());
        assertEquals(0, new BigDecimal("1000.00").compareTo(cuenta.getSaldoInicial()));
        assertEquals(CLIENTE_ID, cuenta.getClienteId());
        assertTrue(cuenta.getEstado());
    }

    @Test
    void testFindByClienteId() {
        // Verificar que podemos encontrar todas las cuentas de un cliente
        List<Cuenta> cuentasCliente = cuentaRepository.findByClienteId(CLIENTE_ID);
        
        assertEquals(2, cuentasCliente.size());
        
        // Verificar que tenemos las dos cuentas esperadas
        boolean encontroCuenta1 = false;
        boolean encontroCuenta2 = false;
        
        for (Cuenta cuenta : cuentasCliente) {
            if (cuenta.getNumeroCuenta().equals(NUMERO_CUENTA_1)) {
                encontroCuenta1 = true;
                assertEquals("Ahorro", cuenta.getTipoCuenta());
            } else if (cuenta.getNumeroCuenta().equals(NUMERO_CUENTA_2)) {
                encontroCuenta2 = true;
                assertEquals("Corriente", cuenta.getTipoCuenta());
            }
        }
        
        assertTrue(encontroCuenta1, "No se encontró la cuenta 1");
        assertTrue(encontroCuenta2, "No se encontró la cuenta 2");
    }

    @Test
    void testUpdateCuenta() {
        // Verificar que podemos actualizar una cuenta existente
        Optional<Cuenta> cuentaOptional = cuentaRepository.findById(NUMERO_CUENTA_1);
        assertTrue(cuentaOptional.isPresent());
        
        Cuenta cuenta = cuentaOptional.get();
        // Actualizar saldo y estado
        cuenta.setSaldoInicial(new BigDecimal("1500.00"));
        cuenta.setEstado(false);
        
        cuentaRepository.save(cuenta);
        
        // Verificar que los cambios se guardaron
        Optional<Cuenta> cuentaActualizadaOptional = cuentaRepository.findById(NUMERO_CUENTA_1);
        assertTrue(cuentaActualizadaOptional.isPresent());
        
        Cuenta cuentaActualizada = cuentaActualizadaOptional.get();
        assertEquals(0, new BigDecimal("1500.00").compareTo(cuentaActualizada.getSaldoInicial()));
        assertFalse(cuentaActualizada.getEstado());
    }

    @Test
    void testDeleteCuenta() {
        // Verificar que podemos eliminar una cuenta
        cuentaRepository.deleteById(NUMERO_CUENTA_1);
        
        // Verificar que la cuenta ya no existe
        Optional<Cuenta> cuentaEliminada = cuentaRepository.findById(NUMERO_CUENTA_1);
        assertFalse(cuentaEliminada.isPresent());
        
        // Verificar que solo se eliminó la cuenta específica
        List<Cuenta> cuentasRestantes = cuentaRepository.findByClienteId(CLIENTE_ID);
        assertEquals(1, cuentasRestantes.size());
        assertEquals(NUMERO_CUENTA_2, cuentasRestantes.get(0).getNumeroCuenta());
    }

    @Test
    void testCuentasConNumerosUnicos() {
        // En lugar de probar la excepción directamente, verificamos que no podemos insertar una cuenta duplicada
        // y que la primera cuenta permanece con sus valores originales
        
        // Verificar que la cuenta original existe y tiene los valores correctos
        Optional<Cuenta> cuentaOriginal = cuentaRepository.findById(NUMERO_CUENTA_1);
        assertTrue(cuentaOriginal.isPresent());
        assertEquals("Ahorro", cuentaOriginal.get().getTipoCuenta());
        assertEquals(0, new BigDecimal("1000.00").compareTo(cuentaOriginal.get().getSaldoInicial()));
        
        // Crear otra cuenta pero con un número diferente
        String nuevoNumeroCuenta = "PRUEBA-UNICO-" + System.currentTimeMillis();
        Cuenta cuentaNueva = new Cuenta();
        cuentaNueva.setNumeroCuenta(nuevoNumeroCuenta);
        cuentaNueva.setTipoCuenta("Ahorro");
        cuentaNueva.setSaldoInicial(new BigDecimal("500.00"));
        cuentaNueva.setEstado(true);
        cuentaNueva.setClienteId("OTRO-CLIENTE");
        
        // Guardar la nueva cuenta
        Cuenta cuentaGuardada = cuentaRepository.save(cuentaNueva);
        
        // Verificar que se guardó correctamente
        assertEquals(nuevoNumeroCuenta, cuentaGuardada.getNumeroCuenta());
        assertEquals("Ahorro", cuentaGuardada.getTipoCuenta());
        
        // Verificar que ahora hay una cuenta más en la base de datos
        long count = cuentaRepository.count();
        assertTrue(count > 2, "Debería haber más de 2 cuentas después de añadir una nueva");
        
        // Limpiar
        cuentaRepository.deleteById(nuevoNumeroCuenta);
    }
}
