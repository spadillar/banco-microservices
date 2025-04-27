package com.banco.eventos.channels;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.messaging.Message;

/**
 * Canal de eventos para movimientos usando el nuevo modelo funcional de Spring Cloud Stream
 */
@Configuration
public interface MovimientoEventChannels {
    String MOVIMIENTO_CREATED_OUTPUT = "movimientoCreatedSupplier-out-0";
    String MOVIMIENTO_CREATED_INPUT = "movimientoCreatedConsumer-in-0";
    
    /**
     * Canal para enviar eventos de movimientos creados
     */
    @Bean
    Supplier<Message<?>> movimientoCreatedSupplier();
    
    /**
     * Canal para recibir eventos de movimientos creados
     */
    @Bean
    Consumer<Message<?>> movimientoCreatedConsumer();
}
