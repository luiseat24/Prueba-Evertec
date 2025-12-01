package com.evertec.prueba.service;

import com.evertec.prueba.model.Deuda;
import com.evertec.prueba.repository.DeudaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // ¡Importación necesaria!

@Service
public class FileLoaderService {

    @Autowired
    private DeudaRepository deudaRepository;

    /**
     * Problema 1: Lee el archivo desde el stream del MultipartFile.
     */
    public String loadFile(MultipartFile file) {
        int successCount = 0;
        int errorCount = 0;
        List<String> errorDetails = new ArrayList<>();
        int lineNumber = 0;
        String fileName = file.getOriginalFilename(); // Nombre del archivo para el reporte

        // 1. Lectura del archivo desde el MultipartFile
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Optional<Deuda> parsedDeuda = parseLine(line, lineNumber);

                if (parsedDeuda.isPresent()) {
                    // 3. Inserción de datos
                    deudaRepository.save(parsedDeuda.get());
                    successCount++;
                } else {
                    errorCount++;
                    errorDetails.add("Línea " + lineNumber + ": Fallo en la validación o parsing. Datos recibidos: '" + line + "'");
                }
            }
            // 4. Retorna el reporte después de la lectura
            return generateReport(fileName, successCount, errorCount, errorDetails);

        } catch (IOException e) {
            // Maneja fallos críticos de I/O o si el archivo está corrupto
            return "ERROR I/O: Fallo al leer el archivo subido. Verifique el archivo. Detalle: " + e.getMessage();
        }
    }

    /**
     * 2. Validación de formato de datos (Largo y Tipo)
     */
    private Optional<Deuda> parseLine(String line, int lineNumber) {
        // Usa un split flexible para manejar ; o : como separador
        String[] fields = line.split("[:;]");

        if (fields.length != 6) {
            return Optional.empty();
        }
        try {
            Deuda deuda = new Deuda();

            // Validaciones de Largo
            String idCliente = fields[0].trim();
            if (idCliente.length() > 15) return Optional.empty();
            deuda.setIdCliente(idCliente);

            String nombreCliente = fields[1].trim();
            if (nombreCliente.length() > 60) return Optional.empty();
            deuda.setNombreCliente(nombreCliente);

            String correo = fields[2].trim();
            if (correo.length() > 60 || !correo.contains("@")) return Optional.empty();
            deuda.setCorreo(correo);

            // Validación de Tipo (BigDecimal)
            deuda.setMonto(new BigDecimal(fields[3].trim()));

            String idDeuda = fields[4].trim();
            if (idDeuda.length() > 15 || deudaRepository.existsByIdDeuda(idDeuda)) {
                return Optional.empty();
            }
            deuda.setIdDeuda(idDeuda);

            // Validación de Tipo (Fecha)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            deuda.setFechaVencimiento(LocalDate.parse(fields[5].trim(), formatter));

            return Optional.of(deuda);

        } catch (NumberFormatException e) {
            return Optional.empty();
        } catch (DateTimeParseException e) {
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Retorna un código del proceso explicativo del resultado.
     * Método simple para estudiantes, retornando un String formateado.
     */
    private String generateReport(String fileName, int successCount, int errorCount, List<String> errorDetails) {

        // Encabezado del reporte
        String header = String.format(
                "--- REPORTE DE CARGA DE ARCHIVO ---\n" +
                        "Archivo Procesado: %s\n" +
                        "-------------------------------------\n",
                fileName
        );

        // Resumen de resultados
        String summary = String.format(
                "RESULTADO:\n" +
                        "  Exitosos: %d\n" +
                        "  Fallidos: %d\n",
                successCount,
                errorCount
        );

        // Detalle de errores (solo si hay fallos)
        String errorSection = "";
        if (errorCount > 0) {
            // Obtenemos una muestra de los errores (máximo 5 para simplicidad)
            String detailedErrors = errorDetails.stream()
                    .limit(5)
                    .map(e -> "\t- " + e)
                    .collect(Collectors.joining("\n"));

            if (errorCount > 5) {
                detailedErrors += String.format("\n\t... y %d errores más no mostrados.", errorCount - 5);
            }

            errorSection = String.format("\nDETALLES DEL FALLO:\n%s", detailedErrors);
        }

        // Combina todas las partes en un solo String final
        return header + summary + errorSection;
    }

}