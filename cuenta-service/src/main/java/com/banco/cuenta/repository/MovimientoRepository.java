package com.banco.cuenta.repository;

import com.banco.cuenta.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByNumeroCuentaAndFechaBetweenOrderByFechaDesc(
        String numeroCuenta, 
        LocalDateTime fechaInicio, 
        LocalDateTime fechaFin
    );
    
    List<Movimiento> findByNumeroCuentaOrderByFechaDesc(String numeroCuenta);
}
