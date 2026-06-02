package br.com.fasda.erp.service;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@Named
@ApplicationScoped
public class ConfiguracaoService {

    // Guarda as configurações em memória: Chave -> Valor
    private Map<String, String> mapaConfiguracoes = new HashMap<>();

    // @Inject
    // private ConfiguracaoDAO configuracaoDAO;

    @PostConstruct
    public void carregarTodas() {
        // Simulando a busca do banco de dados. 
        // Na prática, você faria: List<Configuracao> lista = configuracaoDAO.listarTodas();
        // e alimentaria o mapa.
        
        // Valores temporários para teste:
        mapaConfiguracoes.put("CAMINHO_UPLOAD_IMAGENS", "/uploads/imagens");
        mapaConfiguracoes.put("PERMITIR_ESTOQUE_NEGATIVO", "false");
        mapaConfiguracoes.put("MARGEM_LUCRO_MINIMA", "15.00");
        mapaConfiguracoes.put("DIAS_CARENCIA_COBRANCA", "5");
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