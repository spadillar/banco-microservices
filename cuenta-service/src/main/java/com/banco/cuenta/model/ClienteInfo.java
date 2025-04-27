package com.banco.cuenta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteInfo {
    
    private String clienteId;
    private String nombre;
    private String identificacion;
    private Boolean activo;
    
    public boolean isActivo() {
        return activo != null && activo;
    }
}
