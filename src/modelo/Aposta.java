package modelo;

import java.time.LocalDateTime;

public class Aposta {
    private Partida partida;
    private Participante participante;
    private int golsMandanteApostado;
    private int golsVisitanteApostado;
    private LocalDateTime dataHoraAposta;

    public Aposta(Partida partida, Participante participante, int golsM, int golsV) throws Exception {
        this.partida = partida;
        this.participante = participante;
        this.golsMandanteApostado = golsM;
        this.golsVisitanteApostado = golsV;
        this.dataHoraAposta = LocalDateTime.now();
        validarPrazoAposta();
    }

    public Aposta(Partida partida, Participante participante, int golsM, int golsV, LocalDateTime data) throws Exception {
        this.partida = partida;
        this.participante = participante;
        this.golsMandanteApostado = golsM;
        this.golsVisitanteApostado = golsV;
        this.dataHoraAposta = data;

        validarPrazoAposta();
    }

    private void validarPrazoAposta() throws Exception {
        LocalDateTime limite = partida.getDataHora().minusMinutes(20);
        if (dataHoraAposta.isAfter(limite)) {
            throw new Exception("Aposta inválida! O prazo encerrou 20 minutos antes do início do jogo.");
        }
    }

    public void calcularPontuacao() {
        if (!partida.isFinalizada()) {
            return; // Só calcula se o admin já registou o resultado real
        }

        int realM = partida.getGolsMandante();
        int realV = partida.getGolsVisitante();
        int pontosGanhos = 0;

        // Caso 1: Acertar o resultado e o placar exato (10 pontos)
        if (realM == golsMandanteApostado && realV == golsVisitanteApostado) {
            pontosGanhos = 10;
        }
        else {
            boolean venceuMandanteReal = realM > realV;
            boolean venceuMandanteAposta = golsMandanteApostado > golsVisitanteApostado;

            boolean venceuVisitanteReal = realV > realM;
            boolean venceuVisitanteAposta = golsVisitanteApostado > golsMandanteApostado;

            boolean empateReal = realM == realV;
            boolean empateAposta = golsMandanteApostado == golsVisitanteApostado;

            if ((venceuMandanteReal && venceuMandanteAposta) ||
                    (venceuVisitanteReal && venceuVisitanteAposta) ||
                    (empateReal && empateAposta)) {
                pontosGanhos = 5;
            }
        }

        // Atribui os pontos ao participante
        participante.adicionarPontos(pontosGanhos);
    }

    // --- GETTERS E SETTERS (ENCAPSULAMENTO) ---
    public Partida getPartida() { return partida; }
    public Participante getParticipante() { return participante; }
}