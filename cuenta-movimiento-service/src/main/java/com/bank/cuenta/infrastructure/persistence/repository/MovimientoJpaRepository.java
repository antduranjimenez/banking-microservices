package com.bank.cuenta.infrastructure.persistence.repository;

import com.bank.cuenta.infrastructure.persistence.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoJpaRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaId(Long cuentaId);

    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.id = :cuentaId " +
            "AND m.fecha BETWEEN :inicio AND :fin ORDER BY m.fecha DESC")
    List<Movimiento> findByCuentaIdAndFechaBetween(
            @Param("cuentaId") Long cuentaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
