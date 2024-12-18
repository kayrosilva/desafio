package com.github.kayrosilva.desafio.model.repository;

import com.github.kayrosilva.desafio.model.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Busca todos os endereços de um cliente
    Page<Endereco> findByClienteId(Long clienteId, Pageable pageable);

    // Busca um endereço específico de um cliente
    Optional<Endereco> findByIdAndClienteId(Long enderecoId, Long clienteId);

    // Busca o endereço principal de um cliente
    Optional<Endereco> findByClienteIdAndPrincipalTrue(Long clienteId);
}
