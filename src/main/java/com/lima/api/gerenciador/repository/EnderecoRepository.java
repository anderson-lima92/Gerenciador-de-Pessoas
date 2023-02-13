package com.lima.api.gerenciador.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lima.api.gerenciador.model.Endereco;
import com.lima.api.gerenciador.model.Pessoa;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long>{
	
	List<Endereco> findByPessoaAndEnderecoPrincipalIsTrue(Pessoa pessoa);

}
