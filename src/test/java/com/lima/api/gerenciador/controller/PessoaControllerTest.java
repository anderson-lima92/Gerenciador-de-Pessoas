package com.lima.api.gerenciador.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.lima.api.gerenciador.dto.PessoaDTO;
import com.lima.api.gerenciador.exception.ErrorResponse;
import com.lima.api.gerenciador.model.Pessoa;
import com.lima.api.gerenciador.service.PessoaService;

@ExtendWith(MockitoExtension.class)
class PessoaControllerTest {
	
	@InjectMocks
	private PessoaController pessoaController;

	@Mock
	private PessoaService pessoaService;

	@Test
	void createPessoa() {
		PessoaDTO pessoaDTO = new PessoaDTO();
	    Pessoa pessoa = new Pessoa();
	    pessoa.setNome("teste");
	    pessoa.setCpf(45219835145L);

	    when(pessoaService.criarPessoa(any())).thenReturn(pessoa);

	    ResponseEntity<Object> response = pessoaController.createPessoa(pessoaDTO);

	    assertEquals(HttpStatus.CREATED, response.getStatusCode());
	    assertEquals(pessoa, response.getBody());
	}
	
	@Test
	void createPessoaException() {
		PessoaDTO pessoaDTO = new PessoaDTO();
	    Pessoa pessoa = new Pessoa();
	    pessoa.setNome("teste");
	    pessoa.setCpf(45219835145L);

	    when(pessoaService.criarPessoa(any())).thenThrow(new RuntimeException("Erro ao criar pessoa"));

	    ResponseEntity<Object> response = pessoaController.createPessoa(pessoaDTO);

	    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	    ErrorResponse errorResponse = (ErrorResponse) response.getBody();
	    assertEquals("Erro ao criar pessoa", errorResponse.getMessage());
	}
	
	@Test
	void BuscarPessoaPorCpf_quandoCpfExiste() {
	    Long cpf = 45219835145L;
	    Pessoa pessoaEsperada = new Pessoa();
	    pessoaEsperada.setCpf(cpf);
	    when(pessoaService.consultarPessoa(cpf)).thenReturn(Optional.of(pessoaEsperada));

	    ResponseEntity<?> response = pessoaController.buscarPessoaPorCpf(cpf);

	    assertEquals(HttpStatus.OK, response.getStatusCode());

	}
	
	@Test
	void BuscarPessoaPorCpf_quandoServiceLancaException() {
	    Long cpf = 45219835145L;
	    String mensagemEsperada = "Erro ao buscar pessoa por CPF";
	    when(pessoaService.consultarPessoa(cpf)).thenThrow(new RuntimeException(mensagemEsperada));

	    ResponseEntity<?> response = pessoaController.buscarPessoaPorCpf(cpf);

	    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	    ErrorResponse errorResponse = (ErrorResponse) response.getBody();
	    assertEquals(mensagemEsperada, errorResponse.getMessage());
	}
	
	@Test
	void AtualizarPessoaPorCpf_Success() {
		PessoaDTO pessoaDTO = new PessoaDTO();
	    Long cpf = 45219835145L;
	    Pessoa pessoa = new Pessoa();
	    pessoa.setCpf(cpf);

	    ResponseEntity<Object> response = pessoaController.updatePessoaPorCpf(cpf, pessoaDTO);

	    assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertEquals("Dados atualizados com sucesso!", response.getBody());
	    verify(pessoaService, atLeastOnce()).atualizarPessoa(cpf, pessoaDTO);
	}
	
	@Test
	void AtualizarPessoaPorCpf_RuntimeException() {
		PessoaDTO pessoaDTO = new PessoaDTO();
	    Long cpf = 45219835145L;
	    Pessoa pessoa = new Pessoa();
	    pessoa.setCpf(cpf);
	    
	    doThrow(new RuntimeException("Erro ao atualizar pessoa")).when(pessoaService).atualizarPessoa(cpf, pessoaDTO);

	    ResponseEntity<Object> response = pessoaController.updatePessoaPorCpf(cpf, pessoaDTO);

	    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	    ErrorResponse errorResponse = (ErrorResponse) response.getBody();
	    assertEquals("Erro ao atualizar pessoa", errorResponse.getMessage());
	}
	
	@Test
	void deletePessoaPorCpf_ComSucesso() {
	    Long cpf = 45219835145L;
	    
	    ResponseEntity<Object> response = pessoaController.deletePessoaPorCpf(cpf);

	    assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertEquals("Deletado com sucesso!", response.getBody());
	    verify(pessoaService).deletarPessoa(cpf);
	}

	@Test
	void deletePessoaPorCpf_PessoaNaoEncontrada() {
	    Long cpf = 45219835145L;
	    doThrow(new RuntimeException("Erro ao deletar pessoa")).when(pessoaService).deletarPessoa(cpf);

	    ResponseEntity<Object> response = pessoaController.deletePessoaPorCpf(cpf);
	
	    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	    ErrorResponse errorResponse = (ErrorResponse) response.getBody();
	    assertEquals("Erro ao deletar pessoa", errorResponse.getMessage());
	}
	
	@Test
	void ListaPessoas_Success() {
		Pessoa pessoa = new Pessoa();
	    List<Pessoa> pessoasEsperadas = new ArrayList<>();
	    pessoasEsperadas.add(pessoa);
	    when(pessoaService.listarTodasPessoas()).thenReturn(pessoasEsperadas);

	    ResponseEntity<Object> response = pessoaController.ListaPessoas();

	    assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertEquals(pessoasEsperadas, response.getBody());
	}
	
	@Test
	void ListaPessoas_Exception() {
	    when(pessoaService.listarTodasPessoas()).thenThrow(new RuntimeException("Erro ao listar pessoas"));

	    ResponseEntity<Object> response = pessoaController.ListaPessoas();

	    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	    assertTrue(response.getBody() instanceof ErrorResponse);
	    ErrorResponse errorResponse = (ErrorResponse) response.getBody();
	    assertEquals("Erro ao listar pessoas", errorResponse.getMessage());
	}
	
	@Test
	void buscaEnderecoPrincipalPorCpf_DeveRetornarEndereco_QuandoEncontrarCpf() {

	    Long cpf = 45219835145L;
	    Map<String, String> enderecoEsperado = new HashMap<>();
	    enderecoEsperado.put("rua", "Rua Teste");
	    enderecoEsperado.put("numero", "123");
	    enderecoEsperado.put("cidade", "Cidade Teste");
	    enderecoEsperado.put("estado", "Estado Teste");
	    enderecoEsperado.put("pais", "Pais Teste");
	    when(pessoaService.buscarEnderecoPrincipalPorCpf(cpf)).thenReturn(enderecoEsperado);

	    ResponseEntity<Object> response = pessoaController.buscaEnderecoPrincipalPorCpf(cpf);

	    assertEquals(HttpStatus.OK, response.getStatusCode());
	    assertEquals(enderecoEsperado, response.getBody());
	}
	
	@Test
	void buscaEnderecoPrincipalPorCpf_DeveRetornarErro_QuandoNaoEncontrarCpf() {
	    Long cpf = 45219835145L;
	    when(pessoaService.buscarEnderecoPrincipalPorCpf(cpf)).thenThrow(new RuntimeException("CPF não encontrado"));

	    ResponseEntity<Object> response = pessoaController.buscaEnderecoPrincipalPorCpf(cpf);

	    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	    ErrorResponse errorResponse = (ErrorResponse) response.getBody();
	    assertEquals("CPF não encontrado", errorResponse.getMessage());
	}

}
