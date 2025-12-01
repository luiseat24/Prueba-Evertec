package com.evertec.prueba.repository;

import com.evertec.prueba.model.Deuda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

public interface DeudaRepository extends JpaRepository<Deuda, Long> {

    // Método para validar que no haya ID de deuda duplicado (usado en FileLoaderService)
    boolean existsByIdDeuda(String idDeuda);

    // -----------------------------------------------------------------
    // MÉTODOS AÑADIDOS PARA ARREGLAR LOS ERRORES DEL CONTROLLER (Líneas 72, 77, 82)
    // -----------------------------------------------------------------

    // Requisito: Filtrar por ID del cliente (usado en línea ~82)
    List<Deuda> findByIdCliente(String idCliente);

    // Requisito: Filtrar por fecha de vencimiento (usado después de la línea ~82)
    List<Deuda> findByFechaVencimiento(LocalDate fechaVencimiento);

    // Requisito: Método combinado para filtrar por ambos (usado en línea ~77)
    List<Deuda> findByIdClienteAndFechaVencimiento(String idCliente, LocalDate fechaVencimiento);
}