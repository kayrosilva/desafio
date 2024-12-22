package com.github.kayrosilva.desafio.service;

import com.github.kayrosilva.desafio.data.DTO.ClienteAtualizacaoDTO;
import com.github.kayrosilva.desafio.data.entity.Cliente;
import com.github.kayrosilva.desafio.data.entity.Endereco;
import com.github.kayrosilva.desafio.data.repository.ClienteRepository;
import com.github.kayrosilva.desafio.service.excessoes.NotFoundException;
import com.github.kayrosilva.desafio.service.excessoes.ValidacaoException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class ClienteService {
    public static final String MENSAGEM_CLIENTE_NAO_ENCONTRADO = "Cliente não encontrado!";

    @Autowired
    private ClienteRepository clienteRepository;

    // Criar um novo cliente
    public Cliente criarCliente(Cliente cliente) throws ValidacaoException {
        if (cliente.getEnderecos() == null || cliente.getEnderecos().isEmpty()) {
        }

        // Verifica se há um endereço marcado como principal
        boolean temEnderecoPrincipal = cliente.getEnderecos()
                .stream()
                .anyMatch(Endereco::getPrincipal);

        // Define o cliente para cada endereço
        for (Endereco endereco : cliente.getEnderecos()) {
            endereco.setCliente(cliente);
        }

        // Caso não haja endereço principal, define o primeiro endereço como principal
        if (!temEnderecoPrincipal) {
            cliente.getEnderecos().get(0).setPrincipal(true);
        }

        // Salva o cliente no banco de dados
        return clienteRepository.save(cliente);
    }

    // 2. Editar um Cliente existente
    public Cliente atualizaCliente(Long clienteId, ClienteAtualizacaoDTO clienteAtualizacaoDTO) throws NotFoundException {
        return clienteRepository.findById(clienteId)
                .map(cliente -> {
                    // Atualiza apenas os campos permitidos
                    cliente.setNome(clienteAtualizacaoDTO.getNome());
                    cliente.setSobrenome(clienteAtualizacaoDTO.getSobrenome());
                    cliente.setNascimento(clienteAtualizacaoDTO.getNascimento());

                    // Salva e retorna o cliente atualizado
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new NotFoundException(MENSAGEM_CLIENTE_NAO_ENCONTRADO));
    }

    // 3. Deletar um Cliente pelo ID
    public void deletar(Long clienteId) throws NotFoundException {
        if (!clienteRepository.existsById(clienteId)) {
            throw new NotFoundException(MENSAGEM_CLIENTE_NAO_ENCONTRADO);
        }
        clienteRepository.deleteById(clienteId);
    }

    // 4. Recuperar um Cliente pelo ID
    public Cliente buscarPorId(Long clienteId) throws NotFoundException {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException(MENSAGEM_CLIENTE_NAO_ENCONTRADO));
    }

    // 5. Listar todos os Clientes (com filtro opcional por idade)
    public Page<Cliente> filtrarPorIdade(
            @RequestParam(required = false) Integer idade,
            @RequestParam(required = false) String tipo,
            Pageable pageable) throws NotFoundException {
        // Verifica se os dois parametros foram fornecidos
        if ((idade == null && tipo != null) || (idade != null && tipo == null)) {
            throw new IllegalArgumentException("Ambos os parâmetros 'idade' e 'tipo' devem ser fornecidos juntos.");
        }

        // Se ambos os parâmetros forem fornecidos, valida o tipo
        if (idade != null && tipo != null) {
            if (!tipo.equalsIgnoreCase("maior") && !tipo.equalsIgnoreCase("menor")) {
                throw new IllegalArgumentException("Tipo deve ser 'maior' ou 'menor'.");
            }
            return clienteRepository.findAllByIdadeFiltered(idade, tipo.toLowerCase(), pageable);
        }

        // Caso nenhum parâmetro seja fornecido, retorna todos os clientes
        return clienteRepository.findAll(pageable);
    }
}

