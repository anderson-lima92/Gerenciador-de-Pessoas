// Event listener para o formulário de cadastrar
document.getElementById('createPessoaForm').addEventListener('submit', function(e){
    e.preventDefault();

    // Obtem valores dos campos de Pessoa
    let nome = document.getElementById('nome').value;
    let cpf = document.getElementById('cpf').value;
    let dataNascimento = document.getElementById('dataNascimento').value;

    // Obtem valores dos campos de Endereço
    let logradouro = document.getElementById('logradouro').value;
    let cep = document.getElementById('cep').value;
    let numero = document.getElementById('numero').value;
    let cidade = document.getElementById('cidade').value;

    // Constroi o objeto formData
    let formData = {
        nome: nome,
        cpf: parseInt(cpf),
        dataNascimento: dataNascimento,
        enderecos: [
            {
                logradouro: logradouro,
                cep: cep,
                numero: numero,
                cidade: cidade,
            }
        ]
    };

    // Requisição POST para criar uma pessoa
    fetch('http://localhost:8080/pessoas', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
    .then(response => {
    	if (!response.ok) {
        	return response.json().then(err => { throw err; });
   		}
    	return response.json();
	})
	.then(data => {
	    // Exibe a mensagem de sucesso
	    var mensagemEl = document.getElementById('mensagemCadastro');
	    mensagemEl.innerText = "Pessoa salva com sucesso!";
	    mensagemEl.style.display = 'block';
	    mensagemEl.style.color = "green";
	    mensagemEl.className = "success";
	    // Limpa o formulário ou redirecionar o usuário, se necessário
	    setTimeout(function() {
			mensagemEl.style.display = 'none';
		}, 5000);
	})
	.catch(error => {
	    console.error('Erro:', error);
	    var mensagemEl = document.getElementById('mensagemCadastro');
	    mensagemEl.innerText = error.message;
	    mensagemEl.style.display = 'block';
	    mensagemEl.style.color = "red";
	    mensagemEl.className = "error";
	    setTimeout(function() {
			mensagemEl.style.display = 'none';
		}, 10000);
	});
});

// Event listener para o formulário de consulta
document.getElementById("consultaPessoaForm").addEventListener("submit", function (event) {
    event.preventDefault();

    // Obtem CPF digitado pelo usuário
    let cpfConsulta = document.getElementById("cpfConsulta").value;

    // Envia requisição ao backend para obter dados da pessoa com o CPF
    fetch('http://localhost:8080/pessoas/' + cpfConsulta, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
    })
    .then(response => {
    	if (!response.ok) {
        	return response.json().then(err => { throw err; });
   		}
    	return response.json();
    })
    .then(data => {
        console.log("Response JSON:", data);
        exibirResultados(data);
    })
	.catch(error => {
	    console.error('Erro:', error);
	    var mensagemEl = document.getElementById('mensagemConsulta');
	    mensagemEl.innerText = error.message;
	    mensagemEl.style.display = 'block';
	    mensagemEl.style.color = "red";
	    mensagemEl.className = "error";
	    setTimeout(function() {
			mensagemEl.style.display = 'none';
		}, 10000);
	});
});


// Função para exibir os resultados na tabela
function exibirResultados(resultados) {
	
	let tbody = document.getElementById("resultadoBody");

	tbody.innerHTML = "";

	// Tratar o JSON de retorno
	if (resultados.enderecos && Array.isArray(resultados.enderecos)) {
		resultados.enderecos.forEach(endereco => {
			let newRow = tbody.insertRow();
			newRow.insertCell(0).textContent = resultados.cpf;
			newRow.insertCell(1).textContent = resultados.nome;
			newRow.insertCell(2).textContent = resultados.dataNascimento;
			newRow.insertCell(3).textContent = endereco.logradouro;
			newRow.insertCell(4).textContent = endereco.cep;
			newRow.insertCell(5).textContent = endereco.numero;
			newRow.insertCell(6).textContent = endereco.cidade;
			newRow.insertCell(7).textContent = endereco.enderecoPrincipal ? "Sim" : "Não";
		});
	} else {
		// Se não houver uma lista de endereços, cria uma linha na tabela com os dados do resultado único
		let newRow = tbody.insertRow();
		newRow.insertCell(0).textContent = resultados.cpf;
		newRow.insertCell(1).textContent = resultados.nome;
		newRow.insertCell(2).textContent = resultados.dataNascimento;
		newRow.insertCell(3).textContent = resultados.enderecos ? resultados.enderecos[0].logradouro : "";
		newRow.insertCell(4).textContent = resultados.enderecos ? resultados.enderecos[0].cep : "";
		newRow.insertCell(5).textContent = resultados.enderecos ? resultados.enderecos[0].numero : "";
		newRow.insertCell(6).textContent = resultados.enderecos ? resultados.enderecos[0].cidade : "";
		newRow.insertCell(7).textContent = resultados.enderecos ? (resultados.enderecos[0].enderecoPrincipal ? "Sim" : "Não") : "";
	}

}

// Função para exibir resultado na tabela
function exibirResultadoNaTabela(tbody, resultado) {
    let newRow = tbody.insertRow();

    // Adicionar células com dados
    newRow.insertCell(0).textContent = resultado.nome;
    newRow.insertCell(1).textContent = resultado.cpf;
    newRow.insertCell(2).textContent = resultado.dataNascimento;

    if (resultado.enderecos && resultado.enderecos.length > 0) {
        // Se houver endereços, exibe o primeiro
        let enderecoPrincipal = resultado.enderecos[0];
        newRow.insertCell(3).textContent = enderecoPrincipal.logradouro;
        newRow.insertCell(4).textContent = enderecoPrincipal.cep;
        newRow.insertCell(5).textContent = enderecoPrincipal.numero;
        newRow.insertCell(6).textContent = enderecoPrincipal.cidade;
        newRow.insertCell(7).textContent = enderecoPrincipal.enderecoPrincipal ? "Sim" : "Não";
    } else {
        // Se não houver endereços, preenche células com valores vazios
        for (let i = 3; i <= 7; i++) {
            newRow.insertCell(i).textContent = "";
        }
    }
}












