package com.banco.cuenta.service;

import com.banco.cuenta.model.ClienteInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ClienteCacheService {
    
    private final Map<String, ClienteInfo> clienteCache = new ConcurrentHashMap<>();
    
    public void addCliente(String clienteId, String nombre, String identificacion) {
        log.debug("Agregando cliente al caché: {}", clienteId);
        ClienteInfo clienteInfo = new ClienteInfo(clienteId, nombre, identificacion, true);
        clienteCache.put(clienteId, clienteInfo);
    }
    
    public void updateCliente(String clienteId, String nombre, String identificacion, Boolean estado) {
        log.debug("Actualizando cliente en el caché: {}", clienteId);
        ClienteInfo clienteInfo = new ClienteInfo(clienteId, nombre, identificacion, estado);
        clienteCache.put(clienteId, clienteInfo);
    }
    
    public Optional<ClienteInfo> getClienteInfo(String clienteId) {
        return Optional.ofNullable(clienteCache.get(clienteId));
    }
    
    public boolean isClienteActivo(String clienteId) {
        return clienteCache.containsKey(clienteId) && Boolean.TRUE.equals(clienteCache.get(clienteId).isActivo());
    }
}
