package com.banco.cliente.mapper;

import com.banco.cliente.dto.ClienteDTO;
import com.banco.cliente.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    
    public Cliente toEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setIdentificacion(dto.getIdentificacion());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setClienteId(dto.getClienteId());
        cliente.setContrasena(dto.getContrasena());
        cliente.setEstado(dto.getEstado());
        return cliente;
    }
    
    public ClienteDTO toDto(Cliente entity) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setGenero(entity.getGenero());
        dto.setEdad(entity.getEdad());
        dto.setIdentificacion(entity.getIdentificacion());
        dto.setDireccion(entity.getDireccion());
        dto.setTelefono(entity.getTelefono());
        dto.setClienteId(entity.getClienteId());
        dto.setContrasena(entity.getContrasena());
        dto.setEstado(entity.getEstado());
        return dto;
    }
}
