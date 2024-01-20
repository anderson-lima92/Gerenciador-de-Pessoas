function openForm(evt, formName) {
    // Esconde todo o conteúdo da aba
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // Remove a classe "active" de todos os links da aba
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Mostra o conteúdo da aba atual e adiciona a classe "active" ao botão que abriu a aba
    document.getElementById(formName).style.display = "block";
    evt.currentTarget.className += " active";
}

// Emula um clique na primeira aba para carregar a página com uma aba já ativa
document.addEventListener('DOMContentLoaded', (event) => {
    document.querySelector('.tablinks').click();
});
