package com.github.kayrosilva.desafio.api.rest;

import com.github.kayrosilva.desafio.service.EnderecoService;
import com.github.kayrosilva.desafio.data.entity.Endereco;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.github.kayrosilva.desafio.service.excessoes.NotFoundException;
import com.github.kayrosilva.desafio.service.excessoes.ValidacaoException;

@RestController
@RequestMapping("/api/clientes/{clienteId}/enderecos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EnderecoController {

    private final EnderecoService enderecoService;

    // 1. Criar um novo endereço associado a um cliente
    @PostMapping
    public ResponseEntity<Endereco> criarEndereco(@PathVariable Long clienteId, @RequestBody Endereco endereco) throws NotFoundException, ValidacaoException {
        Endereco novoEndereco = enderecoService.criarEndereco(clienteId, endereco);
        return ResponseEntity.status(201).body(novoEndereco);
    }

    // 2. Buscar todos os endereços de um cliente pelo ID com paginação
    @GetMapping
    public ResponseEntity<Page<Endereco>> listarEnderecosPorCliente(@PathVariable Long clienteId, Pageable pageable) throws NotFoundException {
        Page<Endereco> enderecos = enderecoService.listarEnderecosPorCliente(clienteId, pageable);
        return ResponseEntity.ok(enderecos);
    }

    // 3. Buscar um endereço específico de um cliente
    @GetMapping("/{enderecoId}")
    public ResponseEntity<Endereco> buscarEnderecoPorId(
            @PathVariable Long clienteId, @PathVariable Long enderecoId) throws NotFoundException {
        Endereco endereco = enderecoService.buscarEnderecoPorId(clienteId, enderecoId);
        return ResponseEntity.ok(endereco);
    }

    // 4. Editar um endereço específico associado a um cliente
    @PutMapping("/{enderecoId}")
    public ResponseEntity<Endereco> atualizarEndereco(
            @PathVariable Long clienteId, @PathVariable Long enderecoId, @RequestBody Endereco enderecoAtualizado) throws NotFoundException {
        Endereco endereco = enderecoService.atualizarEndereco(clienteId, enderecoId, enderecoAtualizado);
        return ResponseEntity.ok(endereco);
    }

    // 5. Deletar um endereço específico de um cliente
    @DeleteMapping("/{enderecoId}")
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long clienteId, @PathVariable Long enderecoId) throws NotFoundException {
        enderecoService.deletarEndereco(clienteId, enderecoId);
        return ResponseEntity.noContent().build();
    }
}

