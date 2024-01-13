package com.lima.api.gerenciador.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lima.api.gerenciador.dto.PessoaDTO;
import com.lima.api.gerenciador.model.Endereco;
import com.lima.api.gerenciador.model.Pessoa;
import com.lima.api.gerenciador.repository.EnderecoRepository;
import com.lima.api.gerenciador.repository.PessoaRepository;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

	@InjectMocks
	private PessoaService pessoaService;

	@Mock
	private PessoaRepository pessoaRepository;

	@Mock
	private EnderecoRepository enderecoRepository;

	@Mock
	private HttpServletRequest request;
	
	@Test
	void criarPessoa() throws ParseException {
		Endereco endereco = new Endereco();
		endereco.setEnderecoPrincipal(true);
		endereco.setCep("89621-589");
		
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		PessoaDTO pessoa = new PessoaDTO();
		pessoa.setCpf(45219835145L);
		pessoa.setEnderecos(enderecos);
		pessoa.setDataNascimento("10-10-2015");

		when(request.getMethod()).thenReturn("POST");

		pessoaService.criarPessoa(pessoa);
		
		assertNotNull(pessoa.getCpf());
		assertEquals(String.valueOf(pessoa.getCpf()).length(), 11);
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
	}
	
	@Test
	void criarPessoa_exception_data_nasc_invalida() throws ParseException {
		Endereco endereco = new Endereco();
		endereco.setEnderecoPrincipal(true);
		endereco.setCep("89621589");
		
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		PessoaDTO pessoa = new PessoaDTO();
		pessoa.setCpf(45219835145L);
		pessoa.setEnderecos(enderecos);
		pessoa.setDataNascimento("102015");

		when(request.getMethod()).thenReturn("POST");

		assertThrows(RuntimeException.class, () -> pessoaService.criarPessoa(pessoa));
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
	
	}
	
	@Test
	void criarPessoa_exception_dois_enderecos_principal() {
		Endereco endereco = new Endereco();
		endereco.setEnderecoPrincipal(true);
		endereco.setCep("89621589");
		
		Endereco endereco2 = new Endereco();
		endereco2.setEnderecoPrincipal(true);
		endereco2.setCep("89621589");
		
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		enderecos.add(endereco2);
		PessoaDTO pessoa = new PessoaDTO();
		pessoa.setCpf(45219835145L);
		pessoa.setEnderecos(enderecos);

		when(request.getMethod()).thenReturn("POST");

		assertThrows(RuntimeException.class, () -> pessoaService.criarPessoa(pessoa));

		assertEquals(endereco.getEnderecoPrincipal(), true);
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
		
	}
	
	@Test
	void criarPessoa_exception_sem_endereco_principal() {
		Endereco endereco = new Endereco();
		endereco.setEnderecoPrincipal(false);
		endereco.setCep("89621589");
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		PessoaDTO pessoa = new PessoaDTO();
		pessoa.setCpf(45219835145L);
		pessoa.setEnderecos(enderecos);

		when(request.getMethod()).thenReturn("POST");

		assertThrows(RuntimeException.class, () -> pessoaService.criarPessoa(pessoa));

		assertEquals(endereco.getEnderecoPrincipal(), false);
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
		
	}

	@Test
	void criarPessoa_exception_cep_invalido() {
		Endereco endereco = new Endereco();
		endereco.setCep("21589");
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		PessoaDTO pessoa = new PessoaDTO();
		pessoa.setCpf(45219835145L);
		pessoa.setEnderecos(enderecos);

		when(request.getMethod()).thenReturn("POST");

		assertThrows(RuntimeException.class, () -> pessoaService.criarPessoa(pessoa));

		assertNotEquals(endereco.getCep().length(), 8);
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
		
	}
	
	@Test
	void criarPessoa_exception_cpf_nulo() {
		PessoaDTO pessoa = new PessoaDTO();
		pessoa.setCpf(null);

		assertThrows(RuntimeException.class, () -> pessoaService.criarPessoa(pessoa));

		assertNull(pessoa.getCpf());
	}

	@Test
	void criarPessoa_exception_cpf_invalido() {
		PessoaDTO pessoa = new PessoaDTO();
		pessoa.setCpf(452198L);

		assertThrows(RuntimeException.class, () -> pessoaService.criarPessoa(pessoa));

		assertNotEquals(pessoa.getCpf(), 11);
	}

	@Test
	void criarPessoa_exception_cpf_existente() {
		PessoaDTO pessoaDTO = new PessoaDTO();
		pessoaDTO.setCpf(45219835145L);
		
		Pessoa pessoa = new Pessoa();

		when(pessoaRepository.findByCpf(any())).thenReturn(Optional.of(pessoa));

		when(request.getMethod()).thenReturn("POST");

		assertThrows(RuntimeException.class, () -> pessoaService.criarPessoa(pessoaDTO));
		
		assertEquals(request.getMethod(), "POST");
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
		
	}
	
	@Test
	void consultarPessoa() {
		Long cpf = 45219835145L;
		
		Pessoa pessoa = new Pessoa();
		pessoa.setCpf(cpf);

		when(pessoaRepository.findByCpf(any())).thenReturn(Optional.of(pessoa));

		when(request.getMethod()).thenReturn("GET");

		pessoaService.consultarPessoa(cpf);
		
		assertNotNull(pessoa.getCpf());
		assertEquals(String.valueOf(pessoa.getCpf()).length(), 11);
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
	}
	
	@Test
	void consultarPessoa_cpf_nao_encontrado() {
		Long cpf = 45219835145L;

		when(pessoaRepository.findByCpf(any())).thenReturn(Optional.empty());

		when(request.getMethod()).thenReturn("GET");

		assertThrows(RuntimeException.class, () -> pessoaService.consultarPessoa(cpf));
		
		assertNotNull(cpf);
		assertEquals(String.valueOf(cpf).length(), 11);
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
	}
	
	@Test
	void atualizarPessoa() {	

		Endereco endereco = new Endereco();
		endereco.setCep("12548963");
		endereco.setEnderecoPrincipal(true);
		
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		
		Pessoa pessoa = new Pessoa();
		
		PessoaDTO pessoaDTO = new PessoaDTO();
		pessoaDTO.setCpf(45219835145L);
		pessoaDTO.setEnderecos(enderecos);
		pessoaDTO.setDataNascimento("31012015");
		
		when(pessoaRepository.findByCpf(any())).thenReturn(Optional.of(pessoa));

		when(request.getMethod()).thenReturn("PUT");

		when(enderecoRepository.findByPessoaAndEnderecoPrincipalIsTrue(any())).thenReturn(enderecos);

		pessoaService.atualizarPessoa(pessoaDTO.getCpf(), pessoaDTO);
		
		assertNotNull(pessoaDTO.getCpf());
		assertEquals(String.valueOf(pessoaDTO.getCpf()).length(), 11);
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
		verify(enderecoRepository,  atLeastOnce()).findByPessoaAndEnderecoPrincipalIsTrue(any());
		
	}
	
	@Test
	void atualizarPessoa_cpf_nao_encontrado() {
		Long cpf = 45219835145L;
		PessoaDTO update = new PessoaDTO();

		when(pessoaRepository.findByCpf(any())).thenReturn(Optional.empty());

		when(request.getMethod()).thenReturn("PUT");

		assertThrows(RuntimeException.class, () -> pessoaService.atualizarPessoa(cpf, update));
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
	}
	
	@Test
	void atualizarPessoa_exception() {
		Long cpf = 4521983L;
		PessoaDTO update = new PessoaDTO();

		assertThrows(RuntimeException.class, () -> pessoaService.atualizarPessoa(cpf, update));
		
		assertNotEquals(String.valueOf(cpf).length(), 11);
	}
	
	@Test
	void deletarPessoa() {
		Long cpf = 45219835145L;
		
		Pessoa pessoa = new Pessoa();
		pessoa.setCpf(45219835145L);

		when(pessoaRepository.findByCpf(any())).thenReturn(Optional.of(pessoa));
		
		when(request.getMethod()).thenReturn("DELETE");

		pessoaService.deletarPessoa(cpf);
		
		assertNotNull(cpf);
		assertEquals(String.valueOf(pessoa.getCpf()).length(), 11);
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
		
	}
	
	@Test
	void deletarPessoa_exception_erro_deletar_pessoa() {
		Long cpf = 45219835145L;

		assertThrows(RuntimeException.class, () -> pessoaService.deletarPessoa(cpf));
		
		assertNotNull(cpf);
		assertNotEquals(cpf, 11);
		
	}
	
	@Test
	void listarTodasPessoas() {
		
		Pessoa pessoa = new Pessoa();
		pessoa.setNome("Homer simpson");
		
		List<Pessoa> pessoas = new ArrayList<>();
		pessoas.add(pessoa);
		
		when(pessoaRepository.findAll()).thenReturn(pessoas);

		pessoaService.listarTodasPessoas();
		
		verify(pessoaRepository).findAll();
		
		assertNotNull(pessoas);
	}
	
	@Test
	void listarTodasPessoas_lista_sem_dados() {
		
		List<Pessoa> pessoas = new ArrayList<>();
		pessoas.isEmpty();
		
		when(pessoaRepository.findAll()).thenReturn(pessoas);
		
		assertThrows(RuntimeException.class, () -> pessoaService.listarTodasPessoas());
		
		verify(pessoaRepository).findAll();
		
		assertTrue(pessoas.isEmpty());
	}
	
	@Test
	void buscarEnderecoPrincipalPorCpf() {
		Long cpf = 45219835145L;
		
		Endereco endereco = new Endereco();
		endereco.setCep("12548963");
		endereco.setNumero(256);
		endereco.setEnderecoPrincipal(true);
		
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		
		Pessoa pessoa = new Pessoa();
		pessoa.setEnderecos(enderecos);
		
		when(request.getMethod()).thenReturn("GET");

		when(pessoaRepository.findByCpf(any())).thenReturn(Optional.of(pessoa));

		when(pessoaRepository.findEnderecoPrincipalByCpf(any())).thenReturn(pessoa);
		
		pessoaService.buscarEnderecoPrincipalPorCpf(cpf);
		
		assertTrue(endereco.getEnderecoPrincipal());

		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
		verify(pessoaRepository,  atLeastOnce()).findEnderecoPrincipalByCpf(any());
		
	}
	
	@Test
	void buscarEnderecoPrincipalPorCpf_sem_endereco_principal() {
		Long cpf = 45219835145L;
		
		Endereco endereco = new Endereco();
		endereco.setCep("12548963");
		endereco.setNumero(256);
		endereco.setEnderecoPrincipal(false);
		
		List<Endereco> enderecos = new ArrayList<>();
		enderecos.add(endereco);
		
		Pessoa pessoa = new Pessoa();
		pessoa.setEnderecos(enderecos);
		
		when(request.getMethod()).thenReturn("GET");

		when(pessoaRepository.findByCpf(any())).thenReturn(Optional.of(pessoa));

		when(pessoaRepository.findEnderecoPrincipalByCpf(any())).thenReturn(pessoa);
		
		assertThrows(RuntimeException.class, () -> pessoaService.buscarEnderecoPrincipalPorCpf(cpf));

		assertFalse(endereco.getEnderecoPrincipal());
		
		verify(pessoaRepository, atLeastOnce()).findByCpf(any());
		verify(request,  atLeastOnce()).getMethod();
		verify(pessoaRepository,  atLeastOnce()).findEnderecoPrincipalByCpf(any());
		
	}
}
