package com.evertec.prueba.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "deudas")
public class Deuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 15)
    private String idCliente;

    @Column(length = 60)
    private String nombreCliente;

    @Column(length = 60)
    private String correo;

    @Column(precision = 20)
    private BigDecimal monto;

    @Column(length = 15, unique = true)
    private String idDeuda;

    private LocalDate fechaVencimiento;
}
