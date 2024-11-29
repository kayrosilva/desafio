package com.github.kayrosilva.desafio.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Getter@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 80, nullable = false)
    private String nome;
    @Column(length = 90, nullable = false)
    private String sobrenome;
    @Column(length = 10, nullable = false)
    private LocalDate nacimento;
}
