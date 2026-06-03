package br.com.fasda.erp.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import br.com.fasda.erp.model.Configuracao;
import br.com.fasda.erp.repository.ConfiguracaoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ApplicationScoped
public class ConfiguracaoService {

    // Guarda as configurações em memória: Chave -> Valor
    private Map<String, String> mapaConfiguracoes = new HashMap<>();

    // @Inject
    private ConfiguracaoRepository configuracaoRepository;

    @PostConstruct
    public void carregarTodas() {
    	List<Configuracao> lista = configuracaoRepository.listarTodas();
        for (Configuracao config : lista) {
            mapaConfiguracoes.put(config.getChave(), config.getValor());
        }
    }

    // Métodos utilitários para ler os valores já convertidos no tipo certo
    public String getCaminhoUpload() {
        return mapaConfiguracoes.getOrDefault("CAMINHO_UPLOAD_IMAGENS", "/uploads");
    }

    public boolean isPermitirEstoqueNegativo() {
        return Boolean.parseBoolean(mapaConfiguracoes.getOrDefault("PERMITIR_ESTOQUE_NEGATIVO", "false"));
    }

    public double getMargemLucroMinima() {
        return Double.parseDouble(mapaConfiguracoes.getOrDefault("MARGEM_LUCRO_MINIMA", "0.0"));
    }

    // Método para atualizar a memória quando o usuário salvar na tela
    public void atualizarConfiguracao(String chave, String novoValor) {
        mapaConfiguracoes.put(chave, novoValor);
    }
}