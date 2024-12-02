package com.github.kayrosilva.desafio.model.api.rest;

import com.github.kayrosilva.desafio.model.entity.Cliente;
import com.github.kayrosilva.desafio.model.repository.ClienteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ClienteController {

    private final ClienteRepository repository;

    // Criar um novo Cliente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente salvar(@RequestBody Cliente cliente) {
        return repository.save(cliente);
    }

    // Editar um Cliente existente
    @PutMapping("/{id}")
    public Cliente editar(@PathVariable Integer id, @RequestBody @Valid Cliente clienteAtualizado) {
        return repository.findById(id).map(cliente -> {
            cliente.setNome(clienteAtualizado.getNome());
            cliente.setSobrenome(clienteAtualizado.getSobrenome());
            cliente.setNacimento(clienteAtualizado.getNacimento());
            return repository.save(cliente);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    // Deletar um Cliente pelo id
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }
        repository.deleteById(id);
    }


    // Recuperar um Cliente pelo ID
    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    // Listar todos os Clientes
    @GetMapping("/filtrar")
    public List<Cliente> filtrarPorIdade(@RequestParam Integer idade,
                                         @RequestParam String tipo) {
        if (!tipo.equalsIgnoreCase("maior") && !tipo.equalsIgnoreCase("menor")) {
            throw new IllegalArgumentException("Tipo deve ser 'maior' ou 'menor'.");
        }
        return repository.findAllByIdadeFiltered(idade, tipo.toLowerCase());
    }

}
