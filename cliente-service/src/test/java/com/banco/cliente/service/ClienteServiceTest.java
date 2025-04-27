package com.banco.cliente.service;

import com.banco.cliente.dto.ClienteDTO;
import com.banco.cliente.mapper.ClienteMapper;
import com.banco.cliente.model.Cliente;
import com.banco.cliente.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;
    
    @Mock
    private ClienteMapper clienteMapper;
    
    @Mock
    private ClienteEventService clienteEventService;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Jose Lema");
        cliente.setGenero("M");
        cliente.setEdad(35);
        cliente.setIdentificacion("12345");
        cliente.setDireccion("Otavalo sn y principal");
        cliente.setTelefono("098254785");
        cliente.setClienteId("JL001");
        cliente.setContrasena("1234");
        cliente.setEstado(true);
        
        clienteDTO = new ClienteDTO();
        clienteDTO.setId(1L);
        clienteDTO.setNombre("Jose Lema");
        clienteDTO.setGenero("M");
        clienteDTO.setEdad(35);
        clienteDTO.setIdentificacion("12345");
        clienteDTO.setDireccion("Otavalo sn y principal");
        clienteDTO.setTelefono("098254785");
        clienteDTO.setClienteId("JL001");
        clienteDTO.setContrasena("1234");
        clienteDTO.setEstado(true);
    }

    @Test
    void whenCreateCliente_thenReturnSavedCliente() {
        when(clienteMapper.toEntity(any(ClienteDTO.class))).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toDto(any(Cliente.class))).thenReturn(clienteDTO);

        ClienteDTO savedClienteDTO = clienteService.createCliente(clienteDTO);

        assertNotNull(savedClienteDTO);
        assertEquals(clienteDTO.getClienteId(), savedClienteDTO.getClienteId());
        verify(clienteRepository).save(any(Cliente.class));
        verify(clienteEventService).publishClienteCreatedEvent(any(Cliente.class));
    }

    @Test
    void whenGetClienteByClienteId_thenReturnCliente() {
        when(clienteRepository.findByClienteId(cliente.getClienteId())).thenReturn(Optional.of(cliente));
        when(clienteMapper.toDto(any(Cliente.class))).thenReturn(clienteDTO);

        Optional<ClienteDTO> foundClienteDTO = clienteService.getClienteByClienteId(cliente.getClienteId());

        assertTrue(foundClienteDTO.isPresent());
        assertEquals(clienteDTO.getClienteId(), foundClienteDTO.get().getClienteId());
        verify(clienteRepository).findByClienteId(cliente.getClienteId());
    }

    @Test
    void whenUpdateCliente_thenReturnUpdatedCliente() {
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(clienteMapper.toEntity(any(ClienteDTO.class))).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toDto(any(Cliente.class))).thenReturn(clienteDTO);

        ClienteDTO updatedClienteDTO = clienteService.updateCliente(cliente.getId(), clienteDTO);

        assertNotNull(updatedClienteDTO);
        assertEquals(clienteDTO.getClienteId(), updatedClienteDTO.getClienteId());
        verify(clienteRepository).save(any(Cliente.class));
        verify(clienteEventService).publishClienteUpdatedEvent(any(Cliente.class));
    }

    @Test
    void whenDeleteCliente_thenNothing() {
        Mockito.lenient().when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        doNothing().when(clienteRepository).deleteById(cliente.getId());

        clienteService.deleteCliente(cliente.getId());

        verify(clienteRepository).deleteById(cliente.getId());
    }
}
