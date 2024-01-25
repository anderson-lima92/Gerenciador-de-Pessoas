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
			newRow.insertCell(1).textContent = resultados.ativo ? "Ativo" : "Inativo";
			newRow.insertCell(2).textContent = resultados.nome;
			newRow.insertCell(3).textContent = resultados.dataNascimento;
			newRow.insertCell(4).textContent = endereco.logradouro;
			newRow.insertCell(5).textContent = endereco.cep;
			newRow.insertCell(6).textContent = endereco.numero;
			newRow.insertCell(7).textContent = endereco.cidade;
			newRow.insertCell(8).textContent = endereco.enderecoPrincipal ? "Sim" : "Não";
		});
	} else {
		// Se não houver uma lista de endereços, cria uma linha na tabela com os dados do resultado único
		let newRow = tbody.insertRow();
		newRow.insertCell(0).textContent = resultados.cpf;
		newRow.insertCell(1).textContent = resultados.ativo ? "Ativo" : "Inativo";
		newRow.insertCell(2).textContent = resultados.nome;
		newRow.insertCell(3).textContent = resultados.dataNascimento;
		newRow.insertCell(4).textContent = resultados.enderecos ? resultados.enderecos[0].logradouro : "";
		newRow.insertCell(5).textContent = resultados.enderecos ? resultados.enderecos[0].cep : "";
		newRow.insertCell(6).textContent = resultados.enderecos ? resultados.enderecos[0].numero : "";
		newRow.insertCell(7).textContent = resultados.enderecos ? resultados.enderecos[0].cidade : "";
		newRow.insertCell(8).textContent = resultados.enderecos ? (resultados.enderecos[0].enderecoPrincipal ? "Sim" : "Não") : "";
	}
}
