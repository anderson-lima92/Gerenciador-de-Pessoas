document.getElementById("listarPessoasForm").addEventListener("submit", function (event) {
    event.preventDefault();

    // Envia requisição ao backend para obter lista de dados
    fetch('http://localhost:8080/pessoas/lista-pessoas', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
    })
    .then(response => {
    	if (!response.ok) {
			limparTabela();
        	return response.json().then(err => { throw err; });
   		}
    	return response.json();
    })
    .then(data => {
        console.log("Response JSON:", data);
        exibirResultadosListar(data);
    })
	.catch(error => {
	    console.error('Erro:', error);
	    var mensagemEl = document.getElementById('mensagemListar');
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
function exibirResultadosListar(resultados) {
    let tbody = document.getElementById("resultadoBodyListar");

    limparTabela();

    // Verifica se a lista de resultados não está vazia
    if (Array.isArray(resultados) && resultados.length > 0) {
        resultados.forEach(resultado => {
            // Cria uma nova linha na tabela para cada objeto da lista
            let newRow = tbody.insertRow();
            newRow.insertCell(0).textContent = resultado.cpf;
            newRow.insertCell(1).textContent = resultado.ativo ? "Ativo" : "Inativo";
            newRow.insertCell(2).textContent = resultado.nome;
            newRow.insertCell(3).textContent = resultado.dataNascimento;
            newRow.insertCell(4).textContent = resultado.enderecos ? resultado.enderecos[0].logradouro : "";
            newRow.insertCell(5).textContent = resultado.enderecos ? resultado.enderecos[0].cep : "";
            newRow.insertCell(6).textContent = resultado.enderecos ? resultado.enderecos[0].numero : "";
            newRow.insertCell(7).textContent = resultado.enderecos ? resultado.enderecos[0].cidade : "";
            newRow.insertCell(8).textContent = resultado.enderecos ? (resultado.enderecos[0].enderecoPrincipal ? "Sim" : "Não") : "";
        });
    }
}

function limparTabela() {
    let tbody = document.getElementById("resultadoBodyListar");
    tbody.innerHTML = "";
}
