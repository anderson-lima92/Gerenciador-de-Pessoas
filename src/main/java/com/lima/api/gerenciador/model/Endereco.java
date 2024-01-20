package com.lima.api.gerenciador.model;


import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.lima.api.gerenciador.util.BooleanToStringConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "endereco")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "logradouro", nullable = false)
	private String logradouro;

	@Column(name = "cep", nullable = false)
	private String cep;

	@Column(name = "numero", nullable = false)
	private String numero;

	@Column(name = "cidade", nullable = false)
	private String cidade;

	@Column(name = "endereco_principal", nullable = false)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean enderecoPrincipal;

	@ManyToOne
	@JoinColumn(name = "pessoa_id")
	private Pessoa pessoa;
    
    public boolean isEnderecoPrincipal() {
    	return this.enderecoPrincipal;
    }
}
