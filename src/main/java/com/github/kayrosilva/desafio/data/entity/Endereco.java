package com.github.kayrosilva.desafio.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor

public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(length = 100, nullable = false)
    private String logradouro;

    @Column(length = 10, nullable = false)
    private String numero;

    @Column(length = 50, nullable = false)
    private String complemento;

    @Column(length = 50, nullable = false)
    private String bairro;

    @Column(length = 50, nullable = false)
    private String cidade;

    @Column(length = 50, nullable = false)
    private String estado;

    @Column(length = 8, nullable = false)
    private String cep;

    @Column(nullable = false)
    private Boolean principal = false;

    @Column(length = 50)
    private String descricao;
}
