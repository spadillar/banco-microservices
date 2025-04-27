package com.banco.cuenta.repository;

import com.banco.cuenta.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, String> {
    List<Cuenta> findByClienteId(String clienteId);
}
