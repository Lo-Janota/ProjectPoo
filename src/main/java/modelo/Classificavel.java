package modelo;

/**
 * Interface que define o contrato para qualquer entidade
 * que possa ser classificada/exibida no ranking do sistema.
 */
public interface Classificavel {

    /**
     * Retorna a pontuação total usada para ordenar o ranking.
     */
    int getPontuacaoTotal();

    /**
     * Exibe as informações de classificação da entidade.
     */
    void exibirClassificacao();
}
