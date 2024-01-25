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
