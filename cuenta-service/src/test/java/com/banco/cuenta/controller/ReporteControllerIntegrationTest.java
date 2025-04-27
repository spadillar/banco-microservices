package com.banco.cuenta.controller;

import com.banco.cuenta.dto.ReporteEstadoCuentaDTO;
import com.banco.cuenta.dto.CuentaReporteDTO;
import com.banco.cuenta.dto.MovimientoReporteDTO;
import com.banco.cuenta.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ReporteController.class})
@ActiveProfiles("test")
public class ReporteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;



    @MockBean
    private ReporteService reporteService;

    private final String CLIENTE_ID = "TEST001";
    private final String NUMERO_CUENTA = "TEST123456";
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        // Configurar fechas para las pruebas
        fechaInicio = LocalDateTime.now().minusDays(30);
        fechaFin = LocalDateTime.now().plusDays(1);
        formatter = DateTimeFormatter.ISO_DATE_TIME;
        
        // Configurar los mocks para el reporte estándar con movimientos
        ReporteEstadoCuentaDTO reporteCompleto = crearReporteCompleto();
        Mockito.when(reporteService.generarReporteEstadoCuenta(
                Mockito.eq(CLIENTE_ID), 
                Mockito.any(LocalDateTime.class), 
                Mockito.any(LocalDateTime.class)))
              .thenReturn(reporteCompleto);
        
        // Configurar mock para cliente sin movimientos
        ReporteEstadoCuentaDTO reporteSinMovimientos = crearReporteSinMovimientos();
        Mockito.when(reporteService.generarReporteEstadoCuenta(
                Mockito.eq("SINMOVIMIENTOS"), 
                Mockito.any(LocalDateTime.class), 
                Mockito.any(LocalDateTime.class)))
              .thenReturn(reporteSinMovimientos);
        
        // Configurar mock para cliente inexistente
        ReporteEstadoCuentaDTO reporteVacio = crearReporteVacio();
        Mockito.when(reporteService.generarReporteEstadoCuenta(
                Mockito.eq("INEXISTENTE"), 
                Mockito.any(LocalDateTime.class), 
                Mockito.any(LocalDateTime.class)))
              .thenReturn(reporteVacio);
    }
    
    private ReporteEstadoCuentaDTO crearReporteCompleto() {
        // Crear movimientos para el reporte
        MovimientoReporteDTO deposito = MovimientoReporteDTO.builder()
                .id(1L)
                .fecha(LocalDateTime.now().minusDays(15))
                .tipoMovimiento("DEPOSITO")
                .valor(new BigDecimal("500.00"))
                .saldoResultante(new BigDecimal("1500.00"))
                .descripcion("Depósito a cuenta " + NUMERO_CUENTA)
                .build();
                
        MovimientoReporteDTO retiro = MovimientoReporteDTO.builder()
                .id(2L)
                .fecha(LocalDateTime.now().minusDays(5))
                .tipoMovimiento("RETIRO")
                .valor(new BigDecimal("-200.00"))
                .saldoResultante(new BigDecimal("1300.00"))
                .descripcion("Retiro de cuenta " + NUMERO_CUENTA)
                .build();
        
        // Crear cuenta para el reporte
        CuentaReporteDTO cuenta = CuentaReporteDTO.builder()
                .numeroCuenta(NUMERO_CUENTA)
                .tipoCuenta("Ahorro")
                .estado(true)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldoFinal(new BigDecimal("1300.00"))
                .totalDebitos(new BigDecimal("200.00"))
                .totalCreditos(new BigDecimal("500.00"))
                .movimientos(Arrays.asList(deposito, retiro))
                .build();
        
        // Crear el reporte completo
        return ReporteEstadoCuentaDTO.builder()
                .clienteId(CLIENTE_ID)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .fechaGeneracion(LocalDateTime.now())
                .totalCuentas(1)
                .saldoTotalInicial(new BigDecimal("1000.00"))
                .saldoTotalFinal(new BigDecimal("1300.00"))
                .totalDebitos(new BigDecimal("200.00"))
                .totalCreditos(new BigDecimal("500.00"))
                .cuentas(Arrays.asList(cuenta))
                .build();
    }
    
    private ReporteEstadoCuentaDTO crearReporteSinMovimientos() {
        // Crear cuenta sin movimientos
        CuentaReporteDTO cuenta = CuentaReporteDTO.builder()
                .numeroCuenta(NUMERO_CUENTA)
                .tipoCuenta("Ahorro")
                .estado(true)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldoFinal(new BigDecimal("1000.00"))
                .totalDebitos(BigDecimal.ZERO)
                .totalCreditos(BigDecimal.ZERO)
                .movimientos(new ArrayList<>())
                .build();
        
        // Crear el reporte sin movimientos
        return ReporteEstadoCuentaDTO.builder()
                .clienteId("SINMOVIMIENTOS")
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .fechaGeneracion(LocalDateTime.now())
                .totalCuentas(1)
                .saldoTotalInicial(new BigDecimal("1000.00"))
                .saldoTotalFinal(new BigDecimal("1000.00"))
                .totalDebitos(BigDecimal.ZERO)
                .totalCreditos(BigDecimal.ZERO)
                .cuentas(Arrays.asList(cuenta))
                .build();
    }
    
    private ReporteEstadoCuentaDTO crearReporteVacio() {
        // Crear reporte para cliente inexistente
        return ReporteEstadoCuentaDTO.builder()
                .clienteId("INEXISTENTE")
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

    @Test
    void testGetReporteEstadoCuenta() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/reportes/estado-cuenta")
                .param("clienteId", CLIENTE_ID)
                .param("fechaInicio", fechaInicio.format(formatter))
                .param("fechaFin", fechaFin.format(formatter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID))
                .andExpect(jsonPath("$.totalCuentas").value(1))
                .andExpect(jsonPath("$.cuentas[0].numeroCuenta").value(NUMERO_CUENTA))
                .andExpect(jsonPath("$.cuentas[0].tipoCuenta").value("Ahorro"))
                .andExpect(jsonPath("$.cuentas[0].movimientos.length()").value(2));
    }

    @Test
    void testGetReporteEstadoCuentaSinMovimientos() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/reportes/estado-cuenta")
                .param("clienteId", "SINMOVIMIENTOS")
                .param("fechaInicio", fechaInicio.format(formatter))
                .param("fechaFin", fechaFin.format(formatter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value("SINMOVIMIENTOS"))
                .andExpect(jsonPath("$.totalCuentas").value(1))
                .andExpect(jsonPath("$.cuentas[0].movimientos").isEmpty());
    }

    @Test
    void testGetReporteEstadoCuentaClienteInexistente() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/reportes/estado-cuenta")
                .param("clienteId", "INEXISTENTE")
                .param("fechaInicio", fechaInicio.format(formatter))
                .param("fechaFin", fechaFin.format(formatter))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value("INEXISTENTE"))
                .andExpect(jsonPath("$.totalCuentas").value(0))
                .andExpect(jsonPath("$.cuentas").isEmpty());
    }
}
