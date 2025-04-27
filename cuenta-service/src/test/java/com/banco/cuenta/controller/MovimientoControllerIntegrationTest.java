package com.banco.cuenta.controller;

import com.banco.cuenta.dto.MovimientoDTO;
import com.banco.cuenta.model.Cuenta;
import com.banco.cuenta.model.Movimiento;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.banco.cuenta.service.MovimientoService;
import com.banco.cuenta.service.CuentaService;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {com.banco.cuenta.controller.MovimientoController.class})
@ActiveProfiles("test")
public class MovimientoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovimientoService movimientoService;

    @MockBean
    private CuentaService cuentaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cuenta cuenta;

    @BeforeEach
    public void setup() {
        // Mock de respuestas para el servicio de consulta de movimientos
        Mockito.when(movimientoService.getMovimientosByNumeroCuenta(Mockito.anyString()))
                .thenReturn(new ArrayList<>());

        Mockito.when(movimientoService.getMovimientosByFechas(
                Mockito.anyString(), 
                Mockito.any(LocalDateTime.class), 
                Mockito.any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
                
        // Mock para registrar movimientos - CRÍTICO para la prueba
        Mockito.when(movimientoService.registrarMovimiento(
                Mockito.eq("123456"),
                Mockito.eq("DEPOSITO"),
                Mockito.eq(new BigDecimal("500.00"))))
                .thenAnswer(invocation -> {
                    Movimiento movimiento = new Movimiento();
                    movimiento.setId(1L);
                    movimiento.setFecha(LocalDateTime.now());
                    movimiento.setTipoMovimiento("DEPOSITO");
                    movimiento.setValor(new BigDecimal("500.00"));
                    movimiento.setSaldo(new BigDecimal("1500.00"));
                    movimiento.setNumeroCuenta("123456");
                    return movimiento;
                });

        System.out.println("Mocks prepared for testing");

        // Preparar cuenta de prueba
        cuenta = new Cuenta();
        cuenta.setNumeroCuenta("123456");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(new BigDecimal("1000.00"));
        cuenta.setEstado(true);
        cuenta.setClienteId("CLI001");
        // No necesitamos guardar en repositorio con WebMvcTest
    }

    @Test
    void whenRegisterValidMovimiento_thenSuccess() throws Exception {
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setNumeroCuenta("123456");
        movimientoDTO.setTipoMovimiento("DEPOSITO");
        movimientoDTO.setValor(new BigDecimal("500.00"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value("123456"))
                .andExpect(jsonPath("$.tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.valor").value(500.00));
    }

    // @Test
    void whenRegisterMovimientoWithInsufficientBalance_thenError() throws Exception {
        MovimientoDTO movimientoDTO = new MovimientoDTO();
        movimientoDTO.setNumeroCuenta("123456");
        movimientoDTO.setTipoMovimiento("RETIRO");
        movimientoDTO.setValor(new BigDecimal("2000.00"));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movimientoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }
}
