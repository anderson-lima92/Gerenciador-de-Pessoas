package com.lima.api.gerenciador.model;


import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	private Integer numero;

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
