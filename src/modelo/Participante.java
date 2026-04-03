package modelo;

public class Participante extends Usuario {
    private int pontuacaoTotal;

    // Construtor
    public Participante(String nome, String email) {
        super(nome, email);
        this.pontuacaoTotal = 0; // Todo participante começa com 0 pontos
    }

    @Override
    public void exibirPerfil() {
        System.out.println("PARTICIPANTE: " + this.nome + " (" + this.email + ") | Pontos: " + this.pontuacaoTotal);
    }

    public void adicionarPontos(int pontos) {
        if (pontos > 0) {
            this.pontuacaoTotal += pontos;
        }
    }

    // --- GETTERS E SETTERS ---
    public int getPontuacaoTotal() {
        return pontuacaoTotal;
    }
}