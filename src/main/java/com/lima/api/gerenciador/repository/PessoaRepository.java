package com.lima.api.gerenciador.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.lima.api.gerenciador.model.Pessoa;

import jakarta.transaction.Transactional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
	Optional<Pessoa> findByCpf(Long cpf);

	Pessoa findEnderecoPrincipalByCpf(Long cpf);
	
    @Modifying
    @Transactional
    void deleteById(Long id);
}
