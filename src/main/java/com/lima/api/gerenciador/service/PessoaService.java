package com.lima.api.gerenciador.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lima.api.gerenciador.dto.PessoaDTO;
import com.lima.api.gerenciador.model.Endereco;
import com.lima.api.gerenciador.model.Pessoa;
import com.lima.api.gerenciador.repository.EnderecoRepository;
import com.lima.api.gerenciador.repository.PessoaRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private HttpServletRequest request;

	public Pessoa criarPessoa(PessoaDTO pessoaDTO) {

		log.info("....................Criando Pessoa....................");

		Pessoa pessoa = new Pessoa();
		pessoa.setNome(pessoaDTO.getNome());
		pessoa.setCpf(pessoaDTO.getCpf());
		pessoa.setDataNascimento(pessoaDTO.getDataNascimento());
		pessoa.setEnderecos(pessoaDTO.getEnderecos());

		try {
			pessoa.setCpf(pessoa.getCpf().replaceAll("[\\s.-]", ""));

			validaCpf(pessoa.getCpf());
			validaEnderecos(pessoa);

			String dataNascimento = validaDataNascimento(pessoa.getDataNascimento());

			pessoa.setDataNascimento(dataNascimento);

			Pessoa pessoaSalva = pessoaRepository.save(pessoa);

			return pessoaSalva;

		} catch (Exception e) {
			throw new RuntimeException("Erro ao salvar pessoa: " + e.getMessage());
		}
	}

	public Optional<Pessoa> consultarPessoa(String cpf) {

		log.info("....................Buscando Pessoa....................");

		try {
			cpf = cpf.replaceAll("[\\s.-]", "");

			validaCpf(cpf);

			Optional<Pessoa> pessoaEncontrada = pessoaRepository.findByCpf(cpf);

			return pessoaEncontrada;

		} catch (Exception e) {
			throw new RuntimeException("Erro ao buscar pessoa: " + e.getMessage());
		}
	}

	public void atualizarPessoa(String cpf, PessoaDTO update) {

		log.info("....................Atualaizando Dados....................");

		try {
			cpf = cpf.replaceAll("[\\s.-]", "");

			validaCpf(cpf);

			Optional<Pessoa> pessoaEncontrada = pessoaRepository.findByCpf(cpf);

			if (pessoaEncontrada.isPresent()) {
				Pessoa pessoa = pessoaEncontrada.get();
				pessoa.setNome(update.getNome());

				String dataNascimento = validaDataNascimento(update.getDataNascimento());

				pessoa.setDataNascimento(dataNascimento);

				pessoa.setEnderecos(update.getEnderecos());

				validaEnderecos(pessoa);

				pessoaRepository.save(pessoa);

				log.info("....................Dados Atualizados com sucesso!....................");

			} else {
				throw new IllegalArgumentException("CPF (" + cpf + ") não encontrado.");
			}

		} catch (Exception e) {
			throw new RuntimeException("Erro ao atualizar dados da pessoa: " + e.getMessage());
		}
	}

	public void deletarPessoa(String cpf) {

		log.info("....................Removendo Pessoa....................");

		try {
			cpf = cpf.replaceAll("[\\s.-]", "");

			validaCpf(cpf);

			Optional<Pessoa> pessoa = pessoaRepository.findByCpf(cpf);

			if (pessoa.isPresent()) {
				pessoaRepository.deleteById(pessoa.get().getId());
			}

		} catch (Exception e) {
			throw new IllegalArgumentException("Erro ao deletar pessoa: " + e.getMessage());
		}
	}

	public List<Pessoa> listarTodasPessoas() {
	    List<Pessoa> pessoas = pessoaRepository.findAll();
	    
	    if (pessoas.isEmpty()) {
	        throw new RuntimeException("Não há dados para esta pesquisa");
	    }
	    
	    return pessoas;
	}

	public Map<String, String> buscarEnderecoPrincipalPorCpf(String cpf) {
		cpf = cpf.replaceAll("[\\s.-]", "");
		validaCpf(cpf);
		Pessoa pessoa = pessoaRepository.findEnderecoPrincipalByCpf(cpf);
		for (Endereco endereco : pessoa.getEnderecos()) {
			if (endereco.isEnderecoPrincipal()) {
				Map<String, String> enderecoPrincipal = new HashMap<>();
				enderecoPrincipal.put("logradouro", endereco.getLogradouro());
				enderecoPrincipal.put("cep", endereco.getCep());
				enderecoPrincipal.put("numero", endereco.getNumero().toString());
				enderecoPrincipal.put("cidade", endereco.getCidade());
				return enderecoPrincipal;
			}
		}
		throw new RuntimeException("Não foi encontrado um endereço principal para esta pessoa.");
	}

	private void validaCpf(String cpf) {
		if (cpf.length() != 11) {
			throw new IllegalArgumentException("CPF inválido: (" + cpf + ") CPF deve conter 11 dígitos.");
		}
		Optional<Pessoa> pessoaExistente = pessoaRepository.findByCpf(cpf);
		if (!(request.getMethod().equals("PUT") || request.getMethod().equals("GET")
				|| request.getMethod().equals("DELETE"))) {
			if (pessoaExistente.isPresent()) {
				throw new IllegalArgumentException("Já existe uma pessoa com o CPF informado....");
			}
		}

		if (request.getMethod().equals("GET") || request.getMethod().equals("DELETE")) {
			if (!pessoaExistente.isPresent()) {
				throw new IllegalArgumentException("CPF (" + cpf + ") não encontrado.");
			}
		}
	}

	private void validaEnderecos(Pessoa pessoa) {
		boolean temEnderecoPrincipal = false;
		int countEnderecoPrincipal = 0;

		for (Endereco endereco : pessoa.getEnderecos()) {
			endereco.setPessoa(pessoa);
			String cep = endereco.getCep();
			cep = formataCep(cep);
			endereco.setCep(cep);

			if (endereco.isEnderecoPrincipal()) {
				temEnderecoPrincipal = true;
				countEnderecoPrincipal++;
			}

			if (request.getMethod().equals("PUT")) {
				if (endereco.isEnderecoPrincipal()) {
					List<Endereco> enderecosAtivos = enderecoRepository.findByPessoaAndEnderecoPrincipalIsTrue(pessoa);

					for (Endereco enderecoAtivo : enderecosAtivos) {
						enderecoAtivo.setEnderecoPrincipal(false);
						enderecoRepository.save(enderecoAtivo);
					}

					endereco.setEnderecoPrincipal(true);
				}
			}
		}

		if (!temEnderecoPrincipal) {
			throw new IllegalArgumentException("Deve haver pelo menos um endereço principal.");
		} else if (countEnderecoPrincipal > 1) {
			throw new IllegalArgumentException("Só é permitido ter um endereço principal.");
		}
	}

	private String formataCep(String cep) {
		if (!cep.contains("-")) {
			if (cep.length() == 8) {
				cep = cep.substring(0, 5) + "-" + cep.substring(5);
			} else {
				throw new IllegalArgumentException("Cep inválido: (" + cep + ") Cep deve conter 8 dígitos numéricos.");
			}
		}
		return cep;
	}

	private String validaDataNascimento(String dataNascimento) {
		try {
			if (dataNascimento.contains("-") || dataNascimento.contains("/") || dataNascimento.contains(".")) {
				dataNascimento = dataNascimento.replaceAll("[-/.]", "");
			}

			dataNascimento = formataDataNascimento(dataNascimento);

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			dateFormat.setLenient(false);
			dateFormat.parse(dataNascimento);

		} catch (ParseException e) {
			throw new IllegalArgumentException("Data de nascimento inválida: " + e.getMessage());
		}

		return dataNascimento;
	}

	private String formataDataNascimento(String dataNascimento) {

		return dataNascimento.substring(0, 2) + "-" + dataNascimento.substring(2, 4) + "-"
				+ dataNascimento.substring(4);
	}
}
