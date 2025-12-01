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

@Controller
public class ReporteController {

    @Autowired
    private DeudaRepository deudaRepository;


    @GetMapping("/reporte")
    public String mostrarReporte(
            @RequestParam(required = false) String idCliente,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha,
            Model model
    ) {
        List<Deuda> deudas;

        if (idCliente != null && fecha != null) {
            deudas = deudaRepository.findByIdClienteAndFechaVencimiento(idCliente, fecha);
        } else if (idCliente != null) {
            deudas = deudaRepository.findByIdCliente(idCliente);
        } else if (fecha != null) {
            deudas = deudaRepository.findByFechaVencimiento(fecha);
        } else {
            deudas = deudaRepository.findAll();
        }


        model.addAttribute("deudas", deudas);
        model.addAttribute("idClienteSeleccionado", idCliente);
        model.addAttribute("fechaSeleccionada", fecha);


        return "reporte-deudas";
    }
}