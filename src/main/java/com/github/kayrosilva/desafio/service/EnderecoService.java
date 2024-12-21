package com.github.kayrosilva.desafio.service;

import com.github.kayrosilva.desafio.data.entity.Cliente;
import com.github.kayrosilva.desafio.data.entity.Endereco;
import com.github.kayrosilva.desafio.data.repository.ClienteRepository;
import com.github.kayrosilva.desafio.data.repository.EnderecoRepository;
import com.github.kayrosilva.desafio.service.excessoes.NotFoundException;
import com.github.kayrosilva.desafio.service.excessoes.ValidacaoException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

    public static final String MENSAGEM_CLIENTE_NAO_ENCONTRADO = "Cliente para criação do endereço não encontrado!";
    public static final String MENSAGEM_ENDERECO_POR_CLIENTE_NAO_ENCONTRADO = "Não existe este endereço para este cliente!";
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private ClienteRepository clienteRepository;


    // 1. Criar um novo endereço associado a um cliente
    public Endereco criarEndereco(Long clienteId, Endereco endereco) throws NotFoundException, ValidacaoException {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException(MENSAGEM_CLIENTE_NAO_ENCONTRADO));

        // Verifica os endereços já existentes do cliente
        List<Endereco> enderecosCliente = enderecoRepository.findByClienteId(clienteId, Pageable.unpaged()).getContent(); // Paginação não usada aqui

        if (enderecosCliente.size() >= 8) {
            new ValidacaoException("O cliente ja possui o número máximo de endereço!");
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
        return enderecoRepository.save(novoEndereco);

    }
    // 2. Buscar todos os endereços de um cliente pelo ID do cliente com paginação
    public Page<Endereco> listarEnderecosPorCliente(Long clienteId, Pageable pageable) throws NotFoundException {
        // Verifica se o cliente existe
        if (!clienteRepository.existsById(clienteId)) {
            throw new NotFoundException(MENSAGEM_CLIENTE_NAO_ENCONTRADO);
        }

        // Consulta os endereços do cliente com paginação
        return enderecoRepository.findByClienteId(clienteId, pageable);
    }

    // 3. Buscar um endereço específico de um cliente
    public Endereco buscarEnderecoPorId(Long clienteId, Long enderecoId) throws NotFoundException {
        return enderecoRepository.findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(()-> new NotFoundException(MENSAGEM_ENDERECO_POR_CLIENTE_NAO_ENCONTRADO));
    }

    // 4. Editar um endereço específico associado a um cliente
    public Endereco atualizarEndereco(Long clienteId, Long enderecoId, Endereco enderecoAtualizado) throws NotFoundException {
        Endereco endereco = enderecoRepository.findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(()-> new NotFoundException(MENSAGEM_ENDERECO_POR_CLIENTE_NAO_ENCONTRADO));

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
        }

        // Atualiza os campos do endereço
        endereco.setPrincipal(enderecoAtualizado.getPrincipal());
        endereco.setLogradouro(enderecoAtualizado.getLogradouro());
        endereco.setNumero(enderecoAtualizado.getNumero());
        endereco.setComplemento(enderecoAtualizado.getComplemento());
        endereco.setBairro(enderecoAtualizado.getBairro());
        endereco.setCidade(enderecoAtualizado.getCidade());
        endereco.setEstado(enderecoAtualizado.getEstado());
        endereco.setCep(enderecoAtualizado.getCep());
        endereco.setDescricao(enderecoAtualizado.getDescricao());

        // Salva o endereço atualizado
        return enderecoRepository.save(endereco);
    }

    // 5. Deletar um endereço específico de um cliente
    public void deletarEndereco(Long clienteId, Long enderecoId) throws NotFoundException {
        // Busca o endereço a ser deletado
        Endereco endereco = enderecoRepository.findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(()-> new NotFoundException(MENSAGEM_ENDERECO_POR_CLIENTE_NAO_ENCONTRADO));

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
    }
}
