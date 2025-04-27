package com.banco.cuenta.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cuentas")
public class Cuenta {
    
    @Id
    @Column(nullable = false, unique = true)
    private String numeroCuenta;
    
    @Column(nullable = false)
    private String tipoCuenta;
    
    @Column(nullable = false)
    private BigDecimal saldoInicial;
    
    @Column(nullable = false)
    private Boolean estado;
    
    @Column(nullable = false)
    private String clienteId;
}
