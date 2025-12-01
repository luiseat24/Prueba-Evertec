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
import java.util.stream.Collectors;

@Service
public class FileLoaderService {

    @Autowired
    private DeudaRepository deudaRepository;


    public String loadFile(MultipartFile file) {
        int successCount = 0;
        int errorCount = 0;
        List<String> errorDetails = new ArrayList<>();
        int lineNumber = 0;
        String fileName = file.getOriginalFilename();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Optional<Deuda> parsedDeuda = parseLine(line, lineNumber);

                if (parsedDeuda.isPresent()) {
                    deudaRepository.save(parsedDeuda.get());
                    successCount++;
                } else {
                    errorCount++;
                    errorDetails.add("Línea " + lineNumber + ": Fallo en la validación o parsing. Datos recibidos: '" + line + "'");
                }
            }
            return generateReport(fileName, successCount, errorCount, errorDetails);

        } catch (IOException e) {
            return "ERROR I/O: Fallo al leer el archivo subido. Verifique el archivo. Detalle: " + e.getMessage();
        }
    }


    private Optional<Deuda> parseLine(String line, int lineNumber) {
        String[] fields = line.split("[:;]");

        if (fields.length != 6) {
            return Optional.empty();
        }
        try {
            Deuda deuda = new Deuda();

            String idCliente = fields[0].trim();
            if (idCliente.length() > 15) return Optional.empty();
            deuda.setIdCliente(idCliente);

            String nombreCliente = fields[1].trim();
            if (nombreCliente.length() > 60) return Optional.empty();
            deuda.setNombreCliente(nombreCliente);

            String correo = fields[2].trim();
            if (correo.length() > 60 || !correo.contains("@")) return Optional.empty();
            deuda.setCorreo(correo);

            deuda.setMonto(new BigDecimal(fields[3].trim()));

            String idDeuda = fields[4].trim();
            if (idDeuda.length() > 15 || deudaRepository.existsByIdDeuda(idDeuda)) {
                return Optional.empty();
            }
            deuda.setIdDeuda(idDeuda);

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

    private String generateReport(String fileName, int successCount, int errorCount, List<String> errorDetails) {

        String header = String.format(
                "--- REPORTE DE CARGA DE ARCHIVO ---\n" +
                        "Archivo Procesado: %s\n" +
                        "-------------------------------------\n",
                fileName
        );

        String summary = String.format(
                "RESULTADO:\n" +
                        "  Exitosos: %d\n" +
                        "  Fallidos: %d\n",
                successCount,
                errorCount
        );


        String errorSection = "";
        if (errorCount > 0) {

            String detailedErrors = errorDetails.stream()
                    .limit(5)
                    .map(e -> "\t- " + e)
                    .collect(Collectors.joining("\n"));

            if (errorCount > 5) {
                detailedErrors += String.format("\n\t... y %d errores más no mostrados.", errorCount - 5);
            }

            errorSection = String.format("\nDETALLES DEL FALLO:\n%s", detailedErrors);
        }

        return header + summary + errorSection;
    }

}