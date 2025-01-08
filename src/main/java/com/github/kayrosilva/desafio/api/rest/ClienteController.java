package com.github.kayrosilva.desafio.api.rest;

import com.github.kayrosilva.desafio.data.entity.Cliente;
import com.github.kayrosilva.desafio.data.DTO.ClienteAtualizacaoDTO;
import com.github.kayrosilva.desafio.service.ClienteService;
import com.github.kayrosilva.desafio.service.excessoes.NotFoundException;
import com.github.kayrosilva.desafio.service.excessoes.ValidacaoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // 1. Criar um novo Cliente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criarCliente(@RequestBody Cliente cliente) throws ValidacaoException {
        return clienteService.criarCliente(cliente);
    }

    // 2. Editar um Cliente existente
    @PutMapping("/{id}")
    public Cliente editar(@PathVariable Long id, @RequestBody ClienteAtualizacaoDTO clienteDTO) throws NotFoundException {
        return clienteService.atualizaCliente(id, clienteDTO);
    }

    // 3. Deletar um Cliente pelo ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) throws NotFoundException {
        clienteService.deletar(id);
    }


    // 4. Recuperar um Cliente pelo ID
    @GetMapping("/{id}")
    public Cliente buscarPorId(@PathVariable Long clienteId) throws NotFoundException {
        return clienteService.buscarPorId(clienteId);
    }

    // 5. Listar todos os Clientes (com filtro opcional por idade)
    @GetMapping("/filtrar")
    public Page<Cliente> filtrarPorIdade(
            @RequestParam(required = false) Integer idade,   // Filtro de idade
            @RequestParam(required = false) String tipo,     // Filtro de tipo (maior/menor)
            Pageable pageable) throws NotFoundException {    // Paginação
        // Passando os parâmetros de filtro para o serviço, sem modificar o tipo Pageable
        return clienteService.filtrarPorIdade(idade, tipo, pageable);
    }
}
