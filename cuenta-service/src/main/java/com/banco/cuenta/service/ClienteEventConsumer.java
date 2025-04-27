package com.banco.cuenta.service;

import com.banco.eventos.model.ClienteCreatedEvent;
import com.banco.eventos.model.ClienteUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventConsumer {

    private final ClienteCacheService clienteCacheService;

    @Bean
    public Consumer<ClienteCreatedEvent> clienteCreatedConsumer() {
        return event -> {
            log.info("Recibido evento de cliente creado: {}", event.getClienteId());
            // Actualizar el caché local de clientes
            clienteCacheService.addCliente(event.getClienteId(), event.getNombre(), event.getIdentificacion());
        };
    }

    @Bean
    public Consumer<ClienteUpdatedEvent> clienteUpdatedConsumer() {
        return event -> {
            log.info("Recibido evento de cliente actualizado: {}", event.getClienteId());
            // Actualizar el caché local de clientes
            clienteCacheService.updateCliente(event.getClienteId(), event.getNombre(), event.getIdentificacion(), event.getEstado());
        };
    }
}
