package modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Grupo {
    private String nome;
    private List<Participante> participantes;

    // Construtor
    public Grupo(String nome) {
        this.nome = nome;
        this.participantes = new ArrayList<>();
    }

    public void adicionarParticipante(Participante p) throws Exception {
        if (this.participantes.size() > 10) {
            throw new Exception("Regra violada: O grupo '" + this.nome + "' já está cheio (máximo de 10 participantes).");
        }
        this.participantes.add(p);
    }

    public void exibirClassificacao() {
        System.out.println("=========================================");
        System.out.println("   CLASSIFICAÇÃO - GRUPO: " + this.nome.toUpperCase());
        System.out.println("=========================================");
        this.participantes.sort(Comparator.comparingInt(Participante::getPontuacaoTotal).reversed());

        for (int i = 0; i < participantes.size(); i++) {
            Participante p = participantes.get(i);
            System.out.println((i + 1) + "º Lugar - " + p.getNome() + " | Pontos Totais: " + p.getPontuacaoTotal());
        }
        System.out.println("=========================================\n");
    }

    // --- GETTERS E SETTERS ---
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Participante> getParticipantes() { return participantes; }
}