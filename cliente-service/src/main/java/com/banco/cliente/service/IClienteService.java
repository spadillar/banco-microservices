package com.banco.cliente.service;

import com.banco.cliente.dto.ClienteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface IClienteService {
    ClienteDTO createCliente(ClienteDTO clienteDTO);
    Optional<ClienteDTO> getClienteById(Long id);
    ClienteDTO getClienteByIdOrThrow(Long id);
    Optional<ClienteDTO> getClienteByClienteId(String clienteId);
    Page<ClienteDTO> getAllClientes(Pageable pageable);
    List<ClienteDTO> getAllClientes();
    ClienteDTO updateCliente(Long id, ClienteDTO clienteDTO);
    void deleteCliente(Long id);
}
