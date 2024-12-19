package com.github.kayrosilva.desafio.api.rest;

import com.github.kayrosilva.desafio.service.excessoes.NotFoundException;
import com.github.kayrosilva.desafio.data.entity.Endereco;

import com.github.kayrosilva.desafio.service.EnderecoService;
import com.github.kayrosilva.desafio.service.excessoes.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes/{clienteId}/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    // 1. Criar um novo endereço associado a um cliente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Endereco criarEndereco(@PathVariable Long clienteId, @RequestBody Endereco endereco) {
        // Retorna a resposta com detalhes sobre o novo endereço
        try {
            return enderecoService.criarEndereco(clienteId, endereco);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (ValidacaoException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // 2. Buscar todos os endereços de um cliente pelo ID do cliente com paginação
    @GetMapping
    public Page<Endereco> listarEnderecosPorCliente(@PathVariable Long clienteId, Pageable pageable) {
        try {
            return enderecoService.listarEnderecosPorCliente(clienteId, pageable);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // 3. Buscar um endereço específico de um cliente
    @GetMapping("/{enderecoId}")
    public Endereco buscarEnderecoPorId(
            @PathVariable Long clienteId, @PathVariable Long enderecoId) {
        try {
            return enderecoService.buscarEnderecoPorId(clienteId, enderecoId);
        }catch (NotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // 4. Editar um endereço específico associado a um cliente
    @PutMapping("/{enderecoId}")
    public Endereco atualizarEndereco(
            @PathVariable Long clienteId, @PathVariable Long enderecoId, @RequestBody Endereco enderecoAtualizado) {
        try {
            return enderecoService.atualizarEndereco( clienteId,  enderecoId,  enderecoAtualizado);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // 5. Deletar um endereço específico de um cliente
    @DeleteMapping("/{enderecoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarEndereco(
            @PathVariable Long clienteId, @PathVariable Long enderecoId) {
        try {
            enderecoService.deletarEndereco( clienteId,  enderecoId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }
}
