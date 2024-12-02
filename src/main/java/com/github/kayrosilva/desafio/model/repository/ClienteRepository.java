package com.github.kayrosilva.desafio.model.repository;

import com.github.kayrosilva.desafio.model.entity.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    // Busca paginada de clientes maiores de 18 anos
    Page<Cliente> findByNacimentoBefore(LocalDate data, Pageable pageable);

}
