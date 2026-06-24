// Conteúdo do arquivo recursos/js/funcoes.js

function recolherMatrizPermissoes() {
    setTimeout(function() {
        if (PF('wmatrizPermissoes')) {
            PF('wmatrizPermissoes').jq.find('.ui-tree-toggler.ui-icon-triangle-1-s').click();
        }
    }, 150);
}

// Vincula a execução automática assim que a página carregar do zero
$(document).ready(function() {
    recolherMatrizPermissoes();
});