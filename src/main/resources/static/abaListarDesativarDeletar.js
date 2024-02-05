document.getElementById("listarPessoasForm").addEventListener("submit", function (event) {
    event.preventDefault();

	buscarDados();
	let mostrarMensagem = true;
	
});

function limparTabela() {
    let tbody = document.getElementById("resultadoBodyListar");
    tbody.innerHTML = "";
}

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
			newRow.insertCell(9);
			newRow.insertCell(10);
			newRow.insertCell(11);
			
			// Cria um botão "Ativar" e o adiciona à célula de AÇÕES
			let ativarButton = document.createElement("button");
			ativarButton.textContent = "Ativar";
			if (resultado.ativo) {
				ativarButton.classList.add("ativar-button-disabled");
				ativarButton.style.display = "block";
			} else {
				ativarButton.classList.add("ativar-button");
				ativarButton.addEventListener("click", function() {
					const cpf = resultado.cpf;
					const corpoJSON = criarCorpoJSON(resultado);
					ativarItemNoBanco(cpf, corpoJSON);
				});
			}
            newRow.cells[9].appendChild(ativarButton);
			

            // Cria um botão "Desativar" e o adiciona à célula de AÇÕES
            let desativarButton = document.createElement("button");
            desativarButton.textContent = "Desativar";
            
			if (resultado.ativo) {
				desativarButton.classList.add("desativar-button");
				desativarButton.addEventListener("click", function() {
					const cpf = resultado.cpf;
					desativarItemNoBanco(cpf);
				});

			} else {
				desativarButton.classList.add("desativar-button-disabled");
				desativarButton.style.display = "block";
			}

            newRow.cells[10].appendChild(desativarButton);

            // Cria um botão "Excluir" e o adiciona à célula de AÇÕES
            let deletarButton = document.createElement("button");
            deletarButton.textContent = "Excluir";

			if (resultado.ativo) {
				deletarButton.classList.add("excluir-button-disabled");
				deletarButton.style.display = "block";
			} else {
				deletarButton.classList.add("excluir-button");

				deletarButton.addEventListener("click", function() {
					const cpf = resultado.cpf;
					excluirItemNoBanco(cpf);
				});
			}
           
            newRow.cells[11].appendChild(deletarButton);
        });
    }
}

function criarCorpoJSON(item) {
    return {
        "nome": item.nome,
        "dataNascimento": item.dataNascimento,
        "enderecos": [
            {
                "logradouro": item.enderecos[0].logradouro,
                "cep": item.enderecos[0].cep,
                "numero": item.enderecos[0].numero,
                "cidade": item.enderecos[0].cidade,
                "enderecoPrincipal": item.enderecos[0].enderecoPrincipal
            }
        ]
    };
}

function ativarItemNoBanco(cpf, corpoJSON) {
    fetch(`http://localhost:8080/pessoas/${cpf}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(corpoJSON) // Converte o corpo JSON em uma string
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Falha ao ativar o item');
        }
        buscarDados();
    })
    .catch(error => {
        console.error('Erro ao ativar o item:', error);
    });
}

function desativarItemNoBanco(cpf) {
    fetch(`http://localhost:8080/pessoas/desativacao/${cpf}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Falha ao desativar o item');
        }
        buscarDados();
    })
    .catch(error => {
        console.error('Erro ao desativar o item:', error);
    });
}

function excluirItemNoBanco(cpf) {

    fetch(`http://localhost:8080/pessoas/${cpf}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Falha ao excluir o item');
        }
        buscarDadosSemMensagem();
    })
    .catch(error => {
        console.error('Erro ao excluir o item:', error);
    });
}

function buscarDados() {
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
}

function buscarDadosSemMensagem() {
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
	});
}
