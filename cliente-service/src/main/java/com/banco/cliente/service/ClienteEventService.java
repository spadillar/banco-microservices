package com.banco.cliente.service;

import com.banco.cliente.model.Cliente;
import com.banco.eventos.channels.ClienteEventChannels;
import com.banco.eventos.model.ClienteCreatedEvent;
import com.banco.eventos.model.ClienteUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteEventService {

    private final StreamBridge streamBridge;

    public void publishClienteCreatedEvent(Cliente cliente) {
        log.info("Publicando evento ClienteCreatedEvent para clienteId: {}", cliente.getClienteId());
        
        ClienteCreatedEvent event = ClienteCreatedEvent.builder()
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .identificacion(cliente.getIdentificacion())
                .estado(cliente.getEstado())
                .fechaCreacion(LocalDateTime.now())
                .build();
        
        streamBridge.send(ClienteEventChannels.CLIENTE_CREATED_OUTPUT, event);
    }

    public void publishClienteUpdatedEvent(Cliente cliente) {
        log.info("Publicando evento ClienteUpdatedEvent para clienteId: {}", cliente.getClienteId());
        
        ClienteUpdatedEvent event = ClienteUpdatedEvent.builder()
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .identificacion(cliente.getIdentificacion())
                .estado(cliente.getEstado())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        streamBridge.send(ClienteEventChannels.CLIENTE_UPDATED_OUTPUT, event);
    }
}
