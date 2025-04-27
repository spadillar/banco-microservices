package com.banco.cliente.service;

import com.banco.cliente.dto.ClienteDTO;
import com.banco.cliente.exception.BadRequestException;
import com.banco.cliente.exception.ResourceNotFoundException;
import com.banco.cliente.mapper.ClienteMapper;
import com.banco.cliente.model.Cliente;
import com.banco.cliente.repository.ClienteRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    private final ClienteEventService clienteEventService;
    
    @Override
    @CacheEvict(value = {"clientes", "clientesById"}, allEntries = true)
    @CircuitBreaker(name = "clienteService")
    public ClienteDTO createCliente(ClienteDTO clienteDTO) {
        log.debug("Creando nuevo cliente: {}", clienteDTO.getClienteId());
        try {
            Cliente cliente = clienteMapper.toEntity(clienteDTO);
            Cliente savedCliente = clienteRepository.save(cliente);
            
            // Publicar evento de creación de cliente
            clienteEventService.publishClienteCreatedEvent(savedCliente);
            
            return clienteMapper.toDto(savedCliente);
        } catch (DataIntegrityViolationException e) {
            log.error("Error al crear cliente: datos duplicados", e);
            throw new BadRequestException("Ya existe un cliente con ese ID o identificación");
        }
    }

    @Override
    @Cacheable(value = "clientesById", key = "#id")
    @CircuitBreaker(name = "clienteService", fallbackMethod = "getClienteByIdFallback")
    @Retry(name = "clienteService")
    public Optional<ClienteDTO> getClienteById(Long id) {
        log.debug("Buscando cliente por ID: {}", id);
        return clienteRepository.findById(id)
                .map(clienteMapper::toDto);
    }

    public Optional<ClienteDTO> getClienteByIdFallback(Long id, Exception e) {
        log.error("Fallback: Error al obtener cliente por ID: {}", id, e);
        return Optional.empty();
    }

    @Override
    public ClienteDTO getClienteByIdOrThrow(Long id) {
        return clienteRepository.findById(id)
                .map(clienteMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
    }

    @Override
    public Optional<ClienteDTO> getClienteByClienteId(String clienteId) {
        return clienteRepository.findByClienteId(clienteId)
                .map(clienteMapper::toDto);
    }

    @Override
    @Cacheable(value = "clientes")
    @CircuitBreaker(name = "clienteService", fallbackMethod = "getAllClientesFallback")
    public Page<ClienteDTO> getAllClientes(Pageable pageable) {
        log.debug("Obteniendo lista paginada de clientes");
        return clienteRepository.findAll(pageable)
                .map(clienteMapper::toDto);
    }

    public Page<ClienteDTO> getAllClientesFallback(Pageable pageable, Exception e) {
        log.error("Fallback: Error al obtener clientes", e);
        return Page.empty();
    }
    
    @Override
    @Cacheable(value = "clientes")
    @CircuitBreaker(name = "clienteService", fallbackMethod = "getAllClientesListFallback")
    public List<ClienteDTO> getAllClientes() {
        log.debug("Obteniendo lista completa de clientes");
        return clienteRepository.findAll().stream()
                .map(clienteMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public List<ClienteDTO> getAllClientesListFallback(Exception e) {
        log.error("Fallback: Error al obtener lista completa de clientes", e);
        return List.of();
    }

    @Override
    @CacheEvict(value = {"clientes", "clientesById"}, allEntries = true)
    public ClienteDTO updateCliente(Long id, ClienteDTO clienteDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Verificar si el clienteId ya existe para otro cliente
        if (clienteDTO.getClienteId() != null && 
            !cliente.getClienteId().equals(clienteDTO.getClienteId()) && 
            clienteRepository.findByClienteId(clienteDTO.getClienteId()).isPresent()) {
            throw new BadRequestException("El clienteId ya está en uso");
        }

        Cliente clienteDetails = clienteMapper.toEntity(clienteDTO);
        cliente.setNombre(clienteDetails.getNombre());
        cliente.setGenero(clienteDetails.getGenero());
        cliente.setEdad(clienteDetails.getEdad());
        cliente.setIdentificacion(clienteDetails.getIdentificacion());
        cliente.setDireccion(clienteDetails.getDireccion());
        cliente.setTelefono(clienteDetails.getTelefono());
        cliente.setContrasena(clienteDetails.getContrasena());
        cliente.setEstado(clienteDetails.getEstado());

        try {
            Cliente updatedCliente = clienteRepository.save(cliente);
            
            // Publicar evento de actualización de cliente
            clienteEventService.publishClienteUpdatedEvent(updatedCliente);
            
            return clienteMapper.toDto(updatedCliente);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Error al actualizar el cliente: datos duplicados");
        }
    }

    @Override
    public void deleteCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}
