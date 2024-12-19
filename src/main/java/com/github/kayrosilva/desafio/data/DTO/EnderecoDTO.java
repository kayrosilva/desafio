package com.github.kayrosilva.desafio.data.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoDTO {

    @NotBlank(message = "Logradouro é obrigatório.")
    private String logradouro;

    @NotBlank(message = "Número é obrigatório.")
    private String numero;

    private String complemento;

    @NotBlank(message = "Bairro é obrigatório.")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória.")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório.")
    private String estado;

    @NotBlank(message = "CEP é obrigatório.")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 00000-000")
    private String cep;

    private String descricao;

    private Boolean principal;
}
