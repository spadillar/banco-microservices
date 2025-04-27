package com.banco.cliente.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED)
public class Persona {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String genero;
    
    @Column(nullable = false)
    private Integer edad;
    
    @Column(nullable = false, unique = true)
    private String identificacion;
    
    @Column(nullable = false)
    private String direccion;
    
    @Column(nullable = false)
    private String telefono;
}
