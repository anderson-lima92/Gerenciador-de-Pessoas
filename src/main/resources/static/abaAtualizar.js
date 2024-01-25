document.getElementById("atualizarPessoaForm").addEventListener("submit", function (event) {
    event.preventDefault();

    // Obtem CPF digitado pelo usuário
    let cpfConsulta = document.getElementById("cpfConsultaAtualizar").value;

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
        exibirResultadosAtualizar(data);
    })
	.catch(error => {
	    console.error('Erro:', error);
	    var mensagemEl = document.getElementById('mensagemAtualizar');
	    mensagemEl.innerText = error.message;
	    mensagemEl.style.display = 'block';
	    mensagemEl.style.color = "red";
	    mensagemEl.className = "error";
	    setTimeout(function() {
			mensagemEl.style.display = 'none';
		}, 10000);
	});
});

// Função para exibir os resultados na tabela para Atualizar
function exibirResultadosAtualizar(resultados) {
	cpfConsultaAtual = resultados.cpf;
    let tbody = document.getElementById("resultadoConsultaCPFBody");
    tbody.innerHTML = "";

    // Primeiro, mostra o CPF sem ser editável
    let trCpf = document.createElement("tr");
    let tdChaveCpf = document.createElement("td");
    tdChaveCpf.textContent = "CPF";
    let tdValorCpf = document.createElement("td");
    tdValorCpf.textContent = resultados.cpf;
    trCpf.appendChild(tdChaveCpf);
    trCpf.appendChild(tdValorCpf);
    tbody.appendChild(trCpf);
    
    // Mostra o status (ativo/inativo) sem ser editável
    let trStatus = document.createElement("tr");
    let tdChaveStatus = document.createElement("td");
    tdChaveStatus.textContent = "Status";
    let tdValorStatus = document.createElement("td");
    tdValorStatus.textContent = resultados.ativo ? "Ativo" : "Inativo";
    trStatus.appendChild(tdChaveStatus);
    trStatus.appendChild(tdValorStatus);
    tbody.appendChild(trStatus);

    // Em seguida, adiciona informações da pessoa, exceto o CPF
    let infoPessoal = {
        "Nome": resultados.nome,
        "Data de Nascimento": resultados.dataNascimento
    };

    Object.entries(infoPessoal).forEach(([chave, valor]) => {
        let tr = document.createElement("tr");
        let tdChave = document.createElement("td");
        tdChave.textContent = chave;
        let tdValor = document.createElement("td");
        let input = document.createElement("input");
        input.type = "text";
        input.value = valor;
        input.id = "input_" + chave;
        tdValor.appendChild(input);
        tr.appendChild(tdChave);
        tr.appendChild(tdValor);
        tbody.appendChild(tr);
    });

    // Adiciona informações do primeiro endereço, exceto 'id', 'pessoa' e 'enderecoPrincipal'
    let primeiroEndereco = resultados.enderecos[0];
    Object.entries(primeiroEndereco).forEach(([chave, valor]) => {
        if (!['id', 'pessoa', 'enderecoPrincipal'].includes(chave)) {
            let tr = document.createElement("tr");
            let tdChave = document.createElement("td");
            tdChave.textContent = chave;
            let tdValor = document.createElement("td");
            let input = document.createElement("input");
            input.type = "text";
            input.value = valor;
            input.id = "input_" + chave;
            tdValor.appendChild(input);
            tr.appendChild(tdChave);
            tr.appendChild(tdValor);
            tbody.appendChild(tr);
        }
    });
    
    // Armazenar valores iniciais dos campos
    let valoresIniciais = {};
    document.querySelectorAll("#resultadoConsultaCPFBody input").forEach(input => {
        valoresIniciais[input.id] = input.value;
        input.addEventListener('input', () => {
            let todosIguais = true;
            for (let id in valoresIniciais) {
                let campoAtual = document.getElementById(id);
                if (campoAtual.value !== valoresIniciais[id]) {
                    todosIguais = false;
                    break;
                }
            }
            document.getElementById("botaoAtualizar").disabled = todosIguais;
        });
    });
    
    // Exibe o botão Atualizar, mas inicialmente desabilitado
    let botaoAtualizar = document.getElementById("botaoAtualizar");
    botaoAtualizar.style.display = "block";
    botaoAtualizar.disabled = true; // Começa desabilitado
}

// Event listener para o formulário de atualização
document.getElementById("botaoAtualizar").addEventListener("click", function() {
	let dadosAtualizados = {
		nome: document.getElementById("input_Nome").value,
		dataNascimento: document.getElementById("input_Data de Nascimento").value,
		enderecos: [{
			logradouro: document.getElementById("input_logradouro").value,
			cep: document.getElementById("input_cep").value,
			numero: document.getElementById("input_numero").value,
			cidade: document.getElementById("input_cidade").value,
			enderecoPrincipal: true
		}]
	};

	fetch('http://localhost:8080/pessoas/' + cpfConsultaAtual, {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(dadosAtualizados)
	})
	.then(response => {
		if (!response.ok) {
			return response.json().then(err => { throw err; });
		}
		return response.text();
	})
	.then(data => {
	    var mensagemEl = document.getElementById('mensagemAtualizar');
	    mensagemEl.innerText = "Dados atualizados com sucesso!";
	    mensagemEl.style.display = 'block';
	    mensagemEl.style.color = "green";
	    mensagemEl.className = "success";
	    setTimeout(function() {
	        mensagemEl.style.display = 'none';
	    }, 5000);
	    
	    submitAtualizarPessoaForm();
	})
	.catch(error => {
	    console.error('Erro:', error);
	    var mensagemEl = document.getElementById('mensagemAtualizar');
	    mensagemEl.innerText = error.message;
	    mensagemEl.style.display = 'block';
	    mensagemEl.style.color = "red";
	    mensagemEl.className = "error";
	    setTimeout(function() {
			mensagemEl.style.display = 'none';
		}, 10000);
	});

});

// atualiza novamente a tabela para desativar o botão de Atualizar
function submitAtualizarPessoaForm() {
    let cpfConsulta = document.getElementById("cpfConsultaAtualizar").value;

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
        exibirResultadosAtualizar(data);
    })
}