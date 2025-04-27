package com.banco.cuenta.mapper;

import com.banco.cuenta.dto.CuentaDTO;
import com.banco.cuenta.model.Cuenta;
import org.springframework.stereotype.Component;

@Component
public class CuentaMapper {
    
    public Cuenta toEntity(CuentaDTO dto) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado());
        cuenta.setClienteId(dto.getClienteId());
        return cuenta;
    }
    
    public CuentaDTO toDto(Cuenta entity) {
        CuentaDTO dto = new CuentaDTO();
        dto.setNumeroCuenta(entity.getNumeroCuenta());
        dto.setTipoCuenta(entity.getTipoCuenta());
        dto.setSaldoInicial(entity.getSaldoInicial());
        dto.setEstado(entity.getEstado());
        dto.setClienteId(entity.getClienteId());
        return dto;
    }
}
