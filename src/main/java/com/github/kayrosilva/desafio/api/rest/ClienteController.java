package com.github.kayrosilva.desafio.api.rest;

import com.github.kayrosilva.desafio.data.entity.Cliente;
import com.github.kayrosilva.desafio.data.entity.Endereco;
import com.github.kayrosilva.desafio.data.repository.ClienteRepository;
import com.github.kayrosilva.desafio.data.repository.EnderecoRepository;
import com.github.kayrosilva.desafio.data.DTO.ClienteAtualizacaoDTO;

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

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    // 1. Criar um novo Cliente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente salvar(@RequestBody @Valid Cliente cliente) {
        if (cliente.getEnderecos() != null && !cliente.getEnderecos().isEmpty()) {
            boolean temEnderecoPrincipal = cliente.getEnderecos()
                    .stream()
                    .anyMatch(Endereco::getPrincipal);

            for (Endereco endereco : cliente.getEnderecos()) {
                endereco.setCliente(cliente);
            }

            // Se nenhum endereço for explicitamente marcado como principal, define o primeiro como principal
            if (!temEnderecoPrincipal) {
                cliente.getEnderecos().get(0).setPrincipal(true);
            }
        }
        return clienteRepository.save(cliente);
    }

    // 2. Editar um Cliente existente
    @PutMapping("/{id}")
    public Cliente editar(@PathVariable Long id, @RequestBody @Valid ClienteAtualizacaoDTO clienteDTO) {
        return clienteRepository.findById(id).map(cliente -> {
            // Atualiza apenas os campos permitidos (nome, sobrenome e nascimento)
            cliente.setNome(clienteDTO.getNome());
            cliente.setSobrenome(clienteDTO.getSobrenome());
            cliente.setNascimento(clienteDTO.getNascimento());

            return clienteRepository.save(cliente);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    // 3. Deletar um Cliente pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }

    // 4. Recuperar um Cliente pelo ID
    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    // 5. Listar todos os Clientes (com filtro opcional por idade)
    @GetMapping("/filtrar")
    public Page<Cliente> filtrarPorIdade(
            @RequestParam(required = false) Integer idade,
            @RequestParam(required = false) String tipo,
            Pageable pageable) {
        if (idade != null && tipo != null) {
            if (!tipo.equalsIgnoreCase("maior") && !tipo.equalsIgnoreCase("menor")) {
                throw new IllegalArgumentException("Tipo deve ser 'maior' ou 'menor'.");
            }
            return clienteRepository.findAllByIdadeFiltered(idade, tipo.toLowerCase(), pageable);
        }
        return clienteRepository.findAll(pageable);
    }

    // Entidade Cliente
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Endereco> enderecos;
}
