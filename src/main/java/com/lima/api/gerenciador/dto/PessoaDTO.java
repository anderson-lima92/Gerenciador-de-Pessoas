package com.lima.api.gerenciador.dto;

import java.util.List;

import com.lima.api.gerenciador.model.Endereco;

import lombok.Data;

@Data
public class PessoaDTO {
	
	private Long id;
	private String nome;
	private String cpf;
	private String dataNascimento;
	private List<Endereco> enderecos;

}
