package com.banco.cliente.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente extends Persona {
    
    @Column(nullable = false, unique = true)
    private String clienteId;
    
    @Column(nullable = false)
    private String contrasena;
    
    @Column(nullable = false)
    private Boolean estado;
}
