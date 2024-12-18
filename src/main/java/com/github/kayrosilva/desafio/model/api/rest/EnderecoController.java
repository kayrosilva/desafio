package com.github.kayrosilva.desafio.model.api.rest;

import com.github.kayrosilva.desafio.model.entity.Cliente;
import com.github.kayrosilva.desafio.model.entity.Endereco;
import com.github.kayrosilva.desafio.model.repository.ClienteRepository;
import com.github.kayrosilva.desafio.model.repository.EnderecoRepository;

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
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // 1. Criar um novo endereço associado a um cliente
    @PostMapping
    public ResponseEntity<?> criarEndereco(@PathVariable Long clienteId, @RequestBody Endereco endereco) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        // Verifica os endereços já existentes do cliente
        List<Endereco> enderecosCliente = enderecoRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent(); // Paginação não usada aqui

        if (enderecosCliente.size() >= 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cada cliente pode ter no máximo 8 endereços.");
        }

        Endereco novoEndereco = new Endereco();
        novoEndereco.setLogradouro(endereco.getLogradouro());
        novoEndereco.setNumero(endereco.getNumero());
        novoEndereco.setComplemento(endereco.getComplemento());
        novoEndereco.setBairro(endereco.getBairro());
        novoEndereco.setCidade(endereco.getCidade());
        novoEndereco.setEstado(endereco.getEstado());
        novoEndereco.setCep(endereco.getCep());
        novoEndereco.setDescricao(endereco.getDescricao());
        novoEndereco.setCliente(cliente);

        // Se o cliente não tiver endereços, o novo endereço será automaticamente o principal
        if (enderecosCliente.isEmpty()) {
            novoEndereco.setPrincipal(true);
        } else {
            // Se o cliente já tem endereços, não marca como principal, a menos que o novo endereço seja explicitamente marcado
            if (Boolean.TRUE.equals(endereco.getPrincipal())) {
                // Se o novo endereço for marcado como principal, desmarca os outros como principais
                for (Endereco e : enderecosCliente) {
                    if (Boolean.TRUE.equals(e.getPrincipal())) {
                        e.setPrincipal(false);
                        enderecoRepository.save(e);  // Salva a atualização do endereço principal anterior
                    }
                }
                novoEndereco.setPrincipal(true);
            } else {
                novoEndereco.setPrincipal(false);  // Caso contrário, não marca como principal
            }
        }

        // Salva o novo endereço no banco de dados
        Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);

        // Retorna a resposta com detalhes sobre o novo endereço
        return ResponseEntity.status(HttpStatus.CREATED).body(
                String.format("cliente: %s %s (ID: %d). Endereço ID: %d, Logradouro: %s, Numero: %s, Complemento: %s, Bairro: %s, Cidade: %s, Estado: %s, Cep: %s, Descricao: %s",
                        cliente.getNome(), cliente.getSobrenome(), cliente.getId(),
                        enderecoSalvo.getId(), enderecoSalvo.getLogradouro(), enderecoSalvo.getNumero(),
                        enderecoSalvo.getComplemento(), enderecoSalvo.getBairro(), enderecoSalvo.getCidade(),
                        enderecoSalvo.getEstado(), enderecoSalvo.getCep(), enderecoSalvo.getDescricao())
        );
    }

    // 2. Buscar todos os endereços de um cliente pelo ID do cliente com paginação
    @GetMapping
    public ResponseEntity<Page<Endereco>> listarEnderecosPorCliente(@PathVariable Long clienteId, Pageable pageable) {
        // Verifica se o cliente existe
        if (!clienteRepository.existsById(clienteId)) {
            return ResponseEntity.notFound().build();
        }

        // Consulta os endereços do cliente com paginação
        Page<Endereco> enderecos = enderecoRepository.findByClienteId(clienteId, pageable);

        // Retorna a página de endereços
        return ResponseEntity.ok(enderecos);
    }

    // 3. Buscar um endereço específico de um cliente
    @GetMapping("/{enderecoId}")
    public ResponseEntity<Endereco> buscarEnderecoPorId(
            @PathVariable Long clienteId, @PathVariable Long enderecoId) {
        Optional<Endereco> endereco = enderecoRepository.findByIdAndClienteId(enderecoId, clienteId);
        return endereco.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Editar um endereço específico associado a um cliente
    @PutMapping("/{enderecoId}")
    public ResponseEntity<Endereco> atualizarEndereco(
            @PathVariable Long clienteId, @PathVariable Long enderecoId, @RequestBody Endereco enderecoAtualizado) {

        Optional<Endereco> enderecoExistente = enderecoRepository.findByIdAndClienteId(enderecoId, clienteId);
        if (enderecoExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Endereco endereco = enderecoExistente.get();

        // Verifica se o endereço atualizado deve ser principal
        if (Boolean.TRUE.equals(enderecoAtualizado.getPrincipal())) {
            // Marca o novo endereço como principal e os outros como secundários
            List<Endereco> enderecosCliente = enderecoRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent(); // Paginação não usada aqui
            for (Endereco e : enderecosCliente) {
                if (Boolean.TRUE.equals(e.getPrincipal()) && !e.getId().equals(endereco.getId())) {
                    e.setPrincipal(false);
                    enderecoRepository.save(e);
                }
            }
            endereco.setPrincipal(true);
        } else {
            endereco.setPrincipal(false);
        }

        // Atualiza os campos do endereço
        endereco.setLogradouro(enderecoAtualizado.getLogradouro());
        endereco.setNumero(enderecoAtualizado.getNumero());
        endereco.setComplemento(enderecoAtualizado.getComplemento());
        endereco.setBairro(enderecoAtualizado.getBairro());
        endereco.setCidade(enderecoAtualizado.getCidade());
        endereco.setEstado(enderecoAtualizado.getEstado());
        endereco.setCep(enderecoAtualizado.getCep());
        endereco.setDescricao(enderecoAtualizado.getDescricao());

        // Salva o endereço atualizado
        enderecoRepository.save(endereco);
        return ResponseEntity.ok(endereco);
    }

    // 5. Deletar um endereço específico de um cliente
    @DeleteMapping("/{enderecoId}")
    public ResponseEntity<Void> deletarEndereco(
            @PathVariable Long clienteId, @PathVariable Long enderecoId) {

        // Busca o endereço a ser deletado
        Optional<Endereco> enderecoOptional = enderecoRepository.findByIdAndClienteId(enderecoId, clienteId);
        if (enderecoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Endereco endereco = enderecoOptional.get();
        boolean enderecoEraPrincipal = Boolean.TRUE.equals(endereco.getPrincipal());

        // Deleta o endereço
        enderecoRepository.delete(endereco);

        if (enderecoEraPrincipal) {
            List<Endereco> enderecosRestantes = enderecoRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent();

            if (!enderecosRestantes.isEmpty()) {
                Endereco enderecoComMaiorId = enderecosRestantes.stream()
                        .max((e1, e2) -> Long.compare(e1.getId(), e2.getId()))
                        .orElseThrow(); // Garantido que a lista não está vazia

                enderecoComMaiorId.setPrincipal(true);
                enderecoRepository.save(enderecoComMaiorId);
            }
        }

        return ResponseEntity.noContent().build();
    }
}
