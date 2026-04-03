import modelo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<Clube> clubes = new ArrayList<>();
    private static List<Participante> participantes = new ArrayList<>();
    private static List<Grupo> grupos = new ArrayList<>();
    private static List<Partida> partidas = new ArrayList<>();
    private static List<Aposta> apostas = new ArrayList<>();
    private static Campeonato campeonatoAtual;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        System.out.println("==============================================");
        System.out.println(" BEM-VINDO AO SISTEMA DE APOSTAS DE FUTEBOL");
        System.out.println("==============================================");

        // Criando um campeonato padrão para facilitar os testes
        campeonatoAtual = new Campeonato("Brasileirão LPOO");

        // Criando um grupo padrão
        Grupo grupoPrincipal = new Grupo("Galera da Facul");
        grupos.add(grupoPrincipal);

        while (opcao != 0) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Cadastrar Clube");
            System.out.println("2. Cadastrar Participante");
            System.out.println("3. Cadastrar Partida (Jogo)");
            System.out.println("4. Fazer uma Aposta");
            System.out.println("5. Registrar Resultado do Jogo (Admin)");
            System.out.println("6. Ver Classificação do Grupo");
            System.out.println("0. Sair do Sistema");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer do teclado

                switch (opcao) {
                    case 1:
                        System.out.print("Digite o nome do Clube: ");
                        String nomeClube = scanner.nextLine();
                        Clube novoClube = new Clube(nomeClube);
                        campeonatoAtual.adicionarClube(novoClube);
                        clubes.add(novoClube);
                        System.out.println("Clube '" + nomeClube + "' cadastrado com sucesso!");
                        break;

                    case 2:
                        System.out.print("Digite o nome do Participante: ");
                        String nomePart = scanner.nextLine();
                        System.out.print("Digite o email do Participante: ");
                        String emailPart = scanner.nextLine();
                        Participante p = new Participante(nomePart, emailPart);
                        grupoPrincipal.adicionarParticipante(p);
                        participantes.add(p);
                        System.out.println("Participante '" + nomePart + "' cadastrado e adicionado ao grupo!");
                        break;

                    case 3:
                        if (clubes.size() < 2) {
                            System.out.println("Erro: Cadastre pelo menos 2 clubes primeiro!");
                            break;
                        }
                        System.out.println("Clubes disponíveis:");
                        for (int i = 0; i < clubes.size(); i++) {
                            System.out.println(i + " - " + clubes.get(i).getNome());
                        }
                        System.out.print("Escolha o número do time MANDANTE: ");
                        int idxMandante = scanner.nextInt();
                        System.out.print("Escolha o número do time VISITANTE: ");
                        int idxVisitante = scanner.nextInt();

                        // Jogo marcado para amanhã para passar na regra dos 20 min
                        Partida partida = new Partida(clubes.get(idxMandante), clubes.get(idxVisitante), LocalDateTime.now().plusDays(1));
                        campeonatoAtual.registrarPartida(partida);
                        partidas.add(partida);
                        System.out.println("Partida registrada: " + partida.getDescricao());
                        break;

                    case 4:
                        if (participantes.isEmpty() || partidas.isEmpty()) {
                            System.out.println("Erro: Cadastre participantes e partidas primeiro!");
                            break;
                        }
                        System.out.println("Participantes:");
                        for (int i = 0; i < participantes.size(); i++) {
                            System.out.println(i + " - " + participantes.get(i).getNome());
                        }
                        System.out.print("Quem está apostando? (Digite o número): ");
                        int idxPart = scanner.nextInt();

                        System.out.println("Partidas abertas:");
                        for (int i = 0; i < partidas.size(); i++) {
                            if (!partidas.get(i).isFinalizada()) {
                                System.out.println(i + " - " + partidas.get(i).getDescricao());
                            }
                        }
                        System.out.print("Em qual partida? (Digite o número): ");
                        int idxPartida = scanner.nextInt();

                        System.out.print("Gols do Mandante (" + partidas.get(idxPartida).getMandante().getNome() + "): ");
                        int golsM = scanner.nextInt();
                        System.out.print("Gols do Visitante (" + partidas.get(idxPartida).getVisitante().getNome() + "): ");
                        int golsV = scanner.nextInt();

                        Aposta aposta = new Aposta(partidas.get(idxPartida), participantes.get(idxPart), golsM, golsV);
                        apostas.add(aposta);
                        System.out.println("Aposta registrada com sucesso!");
                        break;

                    case 5:
                        if (partidas.isEmpty()) {
                            System.out.println("Nenhuma partida cadastrada.");
                            break;
                        }
                        System.out.println("Qual partida acabou? (Digite o número): ");
                        for (int i = 0; i < partidas.size(); i++) {
                            if (!partidas.get(i).isFinalizada()) {
                                System.out.println(i + " - " + partidas.get(i).getDescricao());
                            }
                        }
                        int idxFim = scanner.nextInt();

                        System.out.print("Qual foi o placar REAL do Mandante? ");
                        int realM = scanner.nextInt();
                        System.out.print("Qual foi o placar REAL do Visitante? ");
                        int realV = scanner.nextInt();

                        Partida jogoFim = partidas.get(idxFim);
                        jogoFim.registrarResultadoReal(realM, realV);
                        System.out.println("Resultado oficial registrado!");

                        // Processa os pontos de todo mundo que apostou nesse jogo
                        for (Aposta a : apostas) {
                            if (a.getPartida() == jogoFim) {
                                a.calcularPontuacao(); // Nota: usando o nome do método que criamos na classe Aposta
                            }
                        }
                        System.out.println("Pontuações calculadas automaticamente!");
                        break;

                    case 6:
                        grupoPrincipal.exibirClassificacao();
                        break;

                    case 0:
                        System.out.println("Saindo do sistema... Até a próxima!");
                        break;

                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (Exception e) {
                // Captura regras violadas (ex: mais de 8 times, limite de participantes, tempo estourado)
                System.out.println("\n[AVISO DE REGRA DE NEGÓCIO]: " + e.getMessage());
                scanner.nextLine(); // Limpa o scanner em caso de erro
            }
        }
        scanner.close();
    }
}