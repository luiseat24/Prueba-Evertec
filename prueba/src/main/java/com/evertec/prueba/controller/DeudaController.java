package com.evertec.prueba.controller;

import com.evertec.prueba.model.Deuda;
import com.evertec.prueba.repository.DeudaRepository;
import com.evertec.prueba.service.FileLoaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/deudas")
@Tag(name = "Deudas de Clientes", description = "API para Consultar, Ingresar, Modificar y Borrar deudas de clientes.")
public class DeudaController {

    @Autowired
    private DeudaRepository deudaRepository;

    @Autowired
    private FileLoaderService fileLoaderService;

    // ----------------------------------------------------------------------
    // PROBLEMA 1: CARGA MASIVA DE ARCHIVOS (MultipartFile)
    // ----------------------------------------------------------------------

    @Operation(summary = "Cargar deudas desde archivo plano", description = "Inicia el proceso de carga y validación subiendo un archivo plano.")
    @PostMapping("/load-file")
    public ResponseEntity<String> cargarArchivo(
            @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            return ResponseEntity.badRequest().body("ERROR: Debe seleccionar un archivo para cargar.");
        }

        try {
            String resultado = fileLoaderService.loadFile(file);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("ERROR CRÍTICO: Fallo al procesar el archivo. Detalle: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ERROR INTERNO INESPERADO: " + e.getMessage());
        }
    }

    @Operation(summary = "Consultar todas las deudas, con opción a filtrar por ID de Cliente o Fecha de Vencimiento.")
    @GetMapping
    public List<Deuda> consultarDeudas(

            @RequestParam(required = false) String idCliente,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha
    ) {

        if (idCliente != null && fecha != null) {
            return deudaRepository.findByIdClienteAndFechaVencimiento(idCliente, fecha);
        }

        if (idCliente != null) {
            return deudaRepository.findByIdCliente(idCliente);
        }

        if (fecha != null) {
            return deudaRepository.findByFechaVencimiento(fecha);
        }

        return deudaRepository.findAll();
    }

    @Operation(summary = "Ingresar una nueva deuda")
    @PostMapping
    public Deuda ingresarDeuda(@RequestBody Deuda deuda) {
        return deudaRepository.save(deuda);
    }

    @Operation(summary = "Modificar una deuda existente por su ID")
    @PutMapping("/{id}")
    public Deuda modificarDeuda(@PathVariable Long id, @RequestBody Deuda deudaDetails) {
        Deuda deudaExistente = deudaRepository.findById(id)
                // Lanza una excepción 404 si la deuda no se encuentra
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Deuda no encontrada con ID: " + id
                ));

        deudaExistente.setIdCliente(deudaDetails.getIdCliente());
        deudaExistente.setNombreCliente(deudaDetails.getNombreCliente());
        deudaExistente.setCorreo(deudaDetails.getCorreo());
        deudaExistente.setMonto(deudaDetails.getMonto());
        deudaExistente.setIdDeuda(deudaDetails.getIdDeuda());
        deudaExistente.setFechaVencimiento(deudaDetails.getFechaVencimiento());

        return deudaRepository.save(deudaExistente);
    }

    @Operation(summary = "Borrar una deuda por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarDeuda(@PathVariable long id) {
        deudaRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}