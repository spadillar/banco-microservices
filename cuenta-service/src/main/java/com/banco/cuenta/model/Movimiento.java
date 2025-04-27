package com.banco.cuenta.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimientos")
public class Movimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(nullable = false)
    private String tipoMovimiento;
    
    @Column(nullable = false)
    private BigDecimal valor;
    
    @Column(nullable = false)
    private BigDecimal saldo;
    
    @Column(nullable = false)
    private String numeroCuenta;
}
