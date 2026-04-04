package modelo;

import java.util.ArrayList;
import java.util.List;

public class Campeonato {
    private String nome;
    private List<Clube> clubes;
    private List<Partida> partidas;

    // Construtor
    public Campeonato() {
        this.clubes = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public Campeonato(String nome) {
        this.nome = nome;
        this.clubes = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public void adicionarClube(Clube clube) throws Exception {
        if (this.clubes.size() >= 8) {
            throw new Exception("Regra violada: O campeonato '" + this.nome + "' já atingiu o limite de 8 clubes.");
        }
        this.clubes.add(clube);
    }

    public void registrarPartida(Partida partida) {
        this.partidas.add(partida);
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public List<Clube> getClubes() { return clubes; }
    public List<Partida> getPartidas() { return partidas; }
}
