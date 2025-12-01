package com.evertec.prueba.repository;

import com.evertec.prueba.model.Deuda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

public interface DeudaRepository extends JpaRepository<Deuda, Long> {


    boolean existsByIdDeuda(String idDeuda);

    
    List<Deuda> findByIdCliente(String idCliente);

    
    List<Deuda> findByFechaVencimiento(LocalDate fechaVencimiento);

    
    List<Deuda> findByIdClienteAndFechaVencimiento(String idCliente, LocalDate fechaVencimiento);
}