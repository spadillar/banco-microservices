package com.banco.cuenta.integration;

import com.banco.cuenta.CuentaServiceApplication;
import com.banco.cuenta.dto.MovimientoDTO;
import com.banco.cuenta.model.Cuenta;
import com.banco.cuenta.model.Movimiento;
import com.banco.cuenta.repository.CuentaRepository;
import com.banco.cuenta.repository.MovimientoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Prueba de integración real que usa la base de datos PostgreSQL
 * y prueba toda la pila de aplicación desde el controlador hasta la base de datos.
 */
import com.banco.cuenta.config.TestConfig;

/**
 * Prueba de integración real que usa la base de datos PostgreSQL
 * y prueba toda la pila de aplicación desde el controlador hasta la base de datos.
 */
@SpringBootTest(classes = {CuentaServiceApplication.class, TestConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // Asegura rollback después de cada prueba
public class MovimientoIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    private final String NUMERO_CUENTA = "INTEG123456";
    private Cuenta cuentaTest;

    @BeforeEach
    void setUp() {
        logger.debug("Starting test setup and loading ApplicationContext");
        // Limpiar datos para evitar conflictos entre pruebas
        List<Movimiento> movimientosExistentes = movimientoRepository.findByNumeroCuentaOrderByFechaDesc(NUMERO_CUENTA);
        movimientoRepository.deleteAll(movimientosExistentes);
        cuentaRepository.findById(NUMERO_CUENTA).ifPresent(cuenta -> cuentaRepository.delete(cuenta));

        // Crear cuenta de prueba en la base de datos real
        cuentaTest = new Cuenta();
        cuentaTest.setNumeroCuenta(NUMERO_CUENTA);
        cuentaTest.setTipoCuenta("Ahorro");
        cuentaTest.setSaldoInicial(new BigDecimal("1000.00"));
        cuentaTest.setEstado(true);
        cuentaTest.setClienteId("CLI001");
        cuentaRepository.save(cuentaTest);
        logger.debug("ApplicationContext loaded or any errors here");
    }

    @AfterEach
    void tearDown() {
        // Limpieza después de las pruebas
        List<Movimiento> movimientosExistentes = movimientoRepository.findByNumeroCuentaOrderByFechaDesc(NUMERO_CUENTA);
        movimientoRepository.deleteAll(movimientosExistentes);
        cuentaRepository.findById(NUMERO_CUENTA).ifPresent(cuenta -> cuentaRepository.delete(cuenta));
    }

    @Test
    void testRegistrarMovimientoEndToEnd() throws Exception {
        // Preparar DTO para el depósito
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setNumeroCuenta(NUMERO_CUENTA);
        movimientoDTO.setTipoMovimiento("DEPOSITO");
        movimientoDTO.setValor(new BigDecimal("500.00"));

        // Ejecutar la solicitud HTTP y verificar respuesta
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value(NUMERO_CUENTA))
                .andExpect(jsonPath("$.tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.valor").value(500.00));

        // Verificar que el depósito se almacenó en la base de datos
        List<Movimiento> movimientos = movimientoRepository.findByNumeroCuentaOrderByFechaDesc(NUMERO_CUENTA);
        assertEquals(1, movimientos.size());
        assertEquals("DEPOSITO", movimientos.get(0).getTipoMovimiento());
        assertEquals(0, new BigDecimal("500.00").compareTo(movimientos.get(0).getValor()));

        // Verificar que el saldo de la cuenta se actualizó correctamente
        Cuenta cuentaActualizada = cuentaRepository.findById(NUMERO_CUENTA).orElseThrow();
        assertEquals(0, new BigDecimal("1500.00").compareTo(cuentaActualizada.getSaldoInicial()));
    }

    @Test
    void testRetiroConSaldoInsuficiente() throws Exception {
        // Preparar DTO para un retiro mayor al saldo disponible
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setNumeroCuenta(NUMERO_CUENTA);
        movimientoDTO.setTipoMovimiento("RETIRO");
        movimientoDTO.setValor(new BigDecimal("2000.00")); // Mayor que el saldo 1000.00

        // Ejecutar la solicitud HTTP y verificar que se recibe un error
        mockMvc.perform(post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isBadRequest()); // Debería devolver 400 Bad Request

        // Verificar que la cuenta no cambió su saldo
        Cuenta cuentaActualizada = cuentaRepository.findById(NUMERO_CUENTA).orElseThrow();
        assertEquals(0, new BigDecimal("1000.00").compareTo(cuentaActualizada.getSaldoInicial()));
        
        // Verificar que no se registró ningún movimiento
        List<Movimiento> movimientos = movimientoRepository.findByNumeroCuentaOrderByFechaDesc(NUMERO_CUENTA);
        assertTrue(movimientos.isEmpty());
    }

    @Test
    void testConsultarMovimientosPorFechas() throws Exception {
        // Registrar movimientos con fechas distintas
        LocalDateTime fechaAntigua = LocalDateTime.now().minusDays(20);
        LocalDateTime fechaReciente = LocalDateTime.now().minusDays(5);
        
        // Crear un depósito en fecha antigua
        registrarMovimientoDirecto(NUMERO_CUENTA, "DEPOSITO", 
                new BigDecimal("200.00"), fechaAntigua);
        
        // Crear un retiro en fecha reciente
        registrarMovimientoDirecto(NUMERO_CUENTA, "RETIRO", 
                new BigDecimal("100.00"), fechaReciente);

        // Consultar movimientos dentro de un rango que incluye ambos
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fechaFin = LocalDateTime.now();
        
        mockMvc.perform(get("/movimientos/reportes")
                .param("clienteId", "CLI001")
                .param("fechaInicio", fechaInicio.toString())
                .param("fechaFin", fechaFin.toString()))
                .andDo(result -> logger.info("Response content: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].saldoDisponible").value(1100.0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
                
        // Consultar solo movimientos recientes
        LocalDateTime fechaInicioReciente = LocalDateTime.now().minusDays(10);
        
        mockMvc.perform(get("/movimientos/reportes")
                .param("clienteId", "CLI001")
                .param("fechaInicio", fechaInicioReciente.toString())
                .param("fechaFin", fechaFin.toString()))
                .andDo(result -> logger.info("Response content: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].saldoDisponible").value(1100.0))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
    
    /**
     * Método utilitario para registrar movimientos directamente en la BD con fecha específica
     */
    private Movimiento registrarMovimientoDirecto(String numeroCuenta, String tipoMovimiento, 
            BigDecimal valor, LocalDateTime fecha) {
        
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta).orElseThrow();
        BigDecimal saldoActual = cuenta.getSaldoInicial();
        BigDecimal nuevoSaldo;
        BigDecimal valorRegistro = valor;
        
        if (tipoMovimiento.equals("RETIRO")) {
            nuevoSaldo = saldoActual.subtract(valor);
            valorRegistro = valor.negate();
        } else {
            nuevoSaldo = saldoActual.add(valor);
        }
        
        cuenta.setSaldoInicial(nuevoSaldo);
        cuentaRepository.save(cuenta);
        
        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(fecha);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setValor(valorRegistro);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setNumeroCuenta(numeroCuenta);
        
        return movimientoRepository.save(movimiento);
    }
}
