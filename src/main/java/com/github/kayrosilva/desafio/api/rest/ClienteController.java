package com.github.kayrosilva.desafio.api.rest;

import com.github.kayrosilva.desafio.data.entity.Cliente;
import com.github.kayrosilva.desafio.data.entity.Endereco;
import com.github.kayrosilva.desafio.data.repository.ClienteRepository;
import com.github.kayrosilva.desafio.data.repository.EnderecoRepository;
import com.github.kayrosilva.desafio.data.DTO.ClienteAtualizacaoDTO;

import com.github.kayrosilva.desafio.service.ClienteService;
import com.github.kayrosilva.desafio.service.excessoes.NotFoundException;
import com.github.kayrosilva.desafio.service.excessoes.ValidacaoException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final ClienteService clienteService;

    // 1. Criar um novo Cliente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criarCliente(
            @RequestBody @Valid Cliente cliente) {
        try {
            return clienteService.criarCliente(cliente);
        } catch (ValidacaoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    // 2. Editar um Cliente existente
    @PutMapping("/{id}")
    public Cliente editar(
            @PathVariable Long id, @RequestBody @Valid ClienteAtualizacaoDTO clienteDTO) {
        try {
            return clienteService.atualizaCliente(id, clienteDTO);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // 3. Deletar um Cliente pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(
            @PathVariable Long clienteId) {
        try {
            clienteService.deletar(clienteId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    // 4. Recuperar um Cliente pelo ID
    @GetMapping("/{id}")
    public Cliente buscarPorId(
            @PathVariable Long clienteId) {
        try {
            return clienteService.buscarPorId(clienteId);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    // 5. Listar todos os Clientes (com filtro opcional por idade)
    @GetMapping("/filtrar")
    public Page<Cliente> filtrarPorIdade(
            @RequestParam(required = false) Integer idade,
            @RequestParam(required = false) String tipo,
            Pageable pageable) {
        try {
            return clienteService.filtrarPorIdade(idade, tipo, pageable);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    // Entidade Cliente
        @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
        private List<Endereco> enderecos;
    }
