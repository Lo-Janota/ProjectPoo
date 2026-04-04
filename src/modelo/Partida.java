package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Partida {
    private Clube mandante;
    private Clube visitante;
    private LocalDateTime dataHora;
    private int golsMandante;
    private int golsVisitante;
    private boolean finalizada;

    // Construtor padrão
    public Partida() {
        this.finalizada = false;
    }

    // Construtor sobrecarregado — recebe a data/hora real da partida
    public Partida(Clube mandante, Clube visitante, LocalDateTime dataHora) {
        this.mandante = mandante;
        this.visitante = visitante;
        this.dataHora = dataHora;
        this.finalizada = false;
    }

    public void registrarResultadoReal(int golsMandante, int golsVisitante) {
        this.golsMandante = golsMandante;
        this.golsVisitante = golsVisitante;
        this.finalizada = true;
    }

    public String getDescricao() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return mandante.getNome() + " X " + visitante.getNome() + " (" + dataHora.format(formato) + ")";
    }

    // --- GETTERS E SETTERS ---
    public Clube getMandante() { return mandante; }
    public Clube getVisitante() { return visitante; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public int getGolsMandante() { return golsMandante; }
    public int getGolsVisitante() { return golsVisitante; }

    public boolean isFinalizada() { return finalizada; }
}
