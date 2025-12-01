package com.evertec.prueba.controller;

import com.evertec.prueba.model.Deuda;
import com.evertec.prueba.repository.DeudaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller // <-- ¡Usamos @Controller!
public class ReporteController {

    @Autowired
    private DeudaRepository deudaRepository;

    /**
     * Endpoint para el Reporte Web. Reutiliza la lógica de filtrado del Repositorio.
     */
    @GetMapping("/reporte")
    public String mostrarReporte(
            @RequestParam(required = false) String idCliente,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha,
            Model model
    ) {
        List<Deuda> deudas;

        // La misma lógica de filtrado del DeudaController, pero aquí en el nuevo controlador:
        if (idCliente != null && fecha != null) {
            deudas = deudaRepository.findByIdClienteAndFechaVencimiento(idCliente, fecha);
        } else if (idCliente != null) {
            deudas = deudaRepository.findByIdCliente(idCliente);
        } else if (fecha != null) {
            deudas = deudaRepository.findByFechaVencimiento(fecha);
        } else {
            deudas = deudaRepository.findAll();
        }

        // 1. Pasa la lista de deudas a la plantilla HTML
        model.addAttribute("deudas", deudas);
        model.addAttribute("idClienteSeleccionado", idCliente); // Para mantener el filtro en el formulario
        model.addAttribute("fechaSeleccionada", fecha);

        // 2. Retorna el nombre de la plantilla HTML (debe estar en src/main/resources/templates/)
        return "reporte-deudas";
    }
}