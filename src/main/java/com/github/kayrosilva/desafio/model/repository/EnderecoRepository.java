package com.github.kayrosilva.desafio.model.repository;

import com.github.kayrosilva.desafio.model.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Busca todos os endereços de um cliente
    List<Endereco> findByClienteId(Long clienteId);

    // Busca um endereço específico de um cliente
    Optional<Endereco> findByIdAndClienteId(Long enderecoId, Long clienteId);

    // Busca o endereço principal de um cliente
    Optional<Endereco> findByClienteIdAndPrincipalTrue(Long clienteId);
}
