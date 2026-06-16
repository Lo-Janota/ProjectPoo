package modelo;

public class Participante extends Usuario implements Classificavel {
    private int pontuacaoTotal;

    public Participante() {
        super();
        this.pontuacaoTotal = 0;
    }

    public Participante(String nome, String email) {
        super(nome, email);
        this.pontuacaoTotal = 0;
    }

    @Override
    public void exibirPerfil() {
        System.out.println("PARTICIPANTE: " + this.nome + " (" + this.email + ") | Pontos: " + this.pontuacaoTotal);
    }

    @Override
    public void exibirClassificacao() {
        System.out.println("Nome: " + this.nome + " | Pontuação: " + this.pontuacaoTotal);
    }

    public void adicionarPontos(int pontos) {
        if (pontos > 0) {
            this.pontuacaoTotal += pontos;
        }
    }

    @Override
    public int getPontuacaoTotal() { return pontuacaoTotal; }

    public void setPontuacaoTotal(int pontuacao) { this.pontuacaoTotal = pontuacao; }
}
