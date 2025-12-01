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

    // 1. INYECCIÓN DE DEPENDENCIAS (Añadidas)
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
        // Validación básica de que se subió un archivo
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            return ResponseEntity.badRequest().body("ERROR: Debe seleccionar un archivo para cargar.");
        }

        try {
            // Pasamos el objeto 'file' al servicio. Retorna un String (el reporte)
            String resultado = fileLoaderService.loadFile(file);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            // Captura el error crítico de I/O lanzado desde el servicio
            return ResponseEntity.internalServerError().body("ERROR CRÍTICO: Fallo al procesar el archivo. Detalle: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ERROR INTERNO INESPERADO: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // PROBLEMA 2 & 3: API CRUD Y FILTRADO
    // ----------------------------------------------------------------------

    // GET - Consultar deudas (con lógica de filtrado para Problema 3)
    @Operation(summary = "Consultar todas las deudas, con opción a filtrar por ID de Cliente o Fecha de Vencimiento.")
    @GetMapping
    public List<Deuda> consultarDeudas(
            // Añadidas las anotaciones para permitir parámetros opcionales y formatear la fecha
            @RequestParam(required = false) String idCliente,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha
    ) {
        // 1. Caso de Filtrado Combinado (ID y Fecha)
        if (idCliente != null && fecha != null) {
            // Debes asegurarte de tener el método en el DeudaRepository
            return deudaRepository.findByIdClienteAndFechaVencimiento(idCliente, fecha);
        }

        // 2. Caso de Filtrado Solo por ID de Cliente
        if (idCliente != null) {
            return deudaRepository.findByIdCliente(idCliente);
        }

        // 3. Caso de Filtrado Solo por Fecha de Vencimiento
        if (fecha != null) {
            return deudaRepository.findByFechaVencimiento(fecha);
        }

        // 4. Caso por Defecto: Listar todo (Requisito 1 del Problema 3)
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

        // Si llega a este punto, la deuda existe, se procede a actualizar:
        deudaExistente.setIdCliente(deudaDetails.getIdCliente());
        deudaExistente.setNombreCliente(deudaDetails.getNombreCliente());
        deudaExistente.setCorreo(deudaDetails.getCorreo());
        deudaExistente.setMonto(deudaDetails.getMonto());
        deudaExistente.setIdDeuda(deudaDetails.getIdDeuda());
        deudaExistente.setFechaVencimiento(deudaDetails.getFechaVencimiento());

        // Guardar y retornar la entidad actualizada.
        return deudaRepository.save(deudaExistente);
    }

    @Operation(summary = "Borrar una deuda por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarDeuda(@PathVariable long id) {
        // En una solución más robusta, deberíamos verificar si existe antes de borrar
        deudaRepository.deleteById(id);
        // Retorna 204 No Content, que es la respuesta estándar para borrado exitoso
        return ResponseEntity.noContent().build();
    }
}