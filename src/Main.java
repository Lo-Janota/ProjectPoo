import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    private static List<Clube> clubes = new ArrayList<>();
    private static List<Participante> participantes = new ArrayList<>();
    private static List<Grupo> grupos = new ArrayList<>();
    private static List<Partida> partidas = new ArrayList<>();
    private static List<Aposta> apostas = new ArrayList<>();

    private static Campeonato campeonatoAtual;
    private static Grupo grupoPrincipal;

    public static void main(String[] args) {
        campeonatoAtual = new Campeonato("Brasileirão LPOO");
        grupoPrincipal = new Grupo("Galera da Facul");
        grupos.add(grupoPrincipal);

        JFrame janela = new JFrame("Sistema de Apostas de Futebol");
        janela.setSize(450, 450);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLocationRelativeTo(null); // Centraliza

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelPrincipal.setLayout(new GridLayout(8, 1, 10, 10)); // 8 linhas, 1 coluna

        JLabel titulo = new JLabel("MENU PRINCIPAL", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        painelPrincipal.add(titulo);

        JButton btn1 = new JButton("1. Cadastrar Clube");
        JButton btn2 = new JButton("2. Cadastrar Participante");
        JButton btn3 = new JButton("3. Cadastrar Partida");
        JButton btn4 = new JButton("4. Fazer uma Aposta");
        JButton btn5 = new JButton("5. Registrar Resultado do Jogo (Admin)");
        JButton btn6 = new JButton("6. Ver Classificação do Grupo");
        JButton btn0 = new JButton("0. Sair");

        btn1.addActionListener(e -> cadastrarClube(janela));
        btn2.addActionListener(e -> cadastrarParticipante(janela));
        btn3.addActionListener(e -> cadastrarPartida(janela));
        btn4.addActionListener(e -> fazerAposta(janela));
        btn5.addActionListener(e -> registrarResultado(janela));
        btn6.addActionListener(e -> verClassificacao(janela));
        btn0.addActionListener(e -> System.exit(0));

        painelPrincipal.add(btn1);
        painelPrincipal.add(btn2);
        painelPrincipal.add(btn3);
        painelPrincipal.add(btn4);
        painelPrincipal.add(btn5);
        painelPrincipal.add(btn6);
        painelPrincipal.add(btn0);

        janela.add(painelPrincipal);
        janela.setVisible(true);
    }

    private static void cadastrarClube(JFrame parent) {
        String nome = JOptionPane.showInputDialog(parent, "Digite o nome do Clube:");
        if (nome != null && !nome.trim().isEmpty()) {
            try {
                Clube c = new Clube(nome);
                campeonatoAtual.adicionarClube(c);
                clubes.add(c);
                JOptionPane.showMessageDialog(parent, "Clube '" + nome + "' cadastrado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, ex.getMessage(), "Erro de Regra", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void cadastrarParticipante(JFrame parent) {
        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        Object[] campos = {"Nome:", txtNome, "Email:", txtEmail};

        int op = JOptionPane.showConfirmDialog(parent, campos, "Novo Participante", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            try {
                Participante p = new Participante(txtNome.getText(), txtEmail.getText());
                grupoPrincipal.adicionarParticipante(p);
                participantes.add(p);
                JOptionPane.showMessageDialog(parent, "Participante cadastrado!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, ex.getMessage(), "Erro de Regra", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void cadastrarPartida(JFrame parent) {
        if (clubes.size() < 2) {
            JOptionPane.showMessageDialog(parent, "Cadastre pelo menos 2 clubes primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomesClubes = clubes.stream().map(Clube::getNome).toArray(String[]::new);
        JComboBox<String> comboM = new JComboBox<>(nomesClubes);
        JComboBox<String> comboV = new JComboBox<>(nomesClubes);

        Object[] campos = {
                "Time Mandante:", comboM,
                "Time Visitante:", comboV
        };

        int op = JOptionPane.showConfirmDialog(parent, campos, "Nova Partida", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            int idxM = comboM.getSelectedIndex();
            int idxV = comboV.getSelectedIndex();

            if (idxM == idxV) {
                JOptionPane.showMessageDialog(parent, "Um time não pode jogar contra ele mesmo!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Partida p = new Partida(clubes.get(idxM), clubes.get(idxV), LocalDateTime.now().plusDays(1));
            campeonatoAtual.registrarPartida(p);
            partidas.add(p);
            JOptionPane.showMessageDialog(parent, "Partida registrada:\n" + p.getDescricao());
        }
    }

    private static void fazerAposta(JFrame parent) {
        if (participantes.isEmpty() || partidas.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Cadastre participantes e partidas abertas primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomesPart = participantes.stream().map(Participante::getNome).toArray(String[]::new);
        String[] descPartidas = partidas.stream()
                .filter(p -> !p.isFinalizada())
                .map(Partida::getDescricao)
                .toArray(String[]::new);

        if (descPartidas.length == 0) {
            JOptionPane.showMessageDialog(parent, "Nenhuma partida em aberto para apostar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<String> comboPart = new JComboBox<>(nomesPart);
        JComboBox<String> comboPartida = new JComboBox<>(descPartidas);
        JTextField txtGolsM = new JTextField("0");
        JTextField txtGolsV = new JTextField("0");

        Object[] campos = {
                "Quem está apostando?", comboPart,
                "Qual Partida?", comboPartida,
                "Gols do Mandante:", txtGolsM,
                "Gols do Visitante:", txtGolsV
        };

        int op = JOptionPane.showConfirmDialog(parent, campos, "Registrar Aposta", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            try {
                int idxPart = comboPart.getSelectedIndex();
                String descEscolhida = (String) comboPartida.getSelectedItem();
                Partida partidaEscolhida = partidas.stream()
                        .filter(p -> p.getDescricao().equals(descEscolhida))
                        .findFirst().orElse(null);

                int golsM = Integer.parseInt(txtGolsM.getText());
                int golsV = Integer.parseInt(txtGolsV.getText());

                Aposta a = new Aposta(partidaEscolhida, participantes.get(idxPart), golsM, golsV);
                apostas.add(a);
                JOptionPane.showMessageDialog(parent, "Aposta registrada com sucesso!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Digite apenas números para os gols!", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, ex.getMessage(), "Erro de Regra", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void registrarResultado(JFrame parent) {
        String[] descPartidas = partidas.stream()
                .filter(p -> !p.isFinalizada())
                .map(Partida::getDescricao)
                .toArray(String[]::new);

        if (descPartidas.length == 0) {
            JOptionPane.showMessageDialog(parent, "Nenhuma partida aguardando resultado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<String> comboPartida = new JComboBox<>(descPartidas);
        JTextField txtGolsM = new JTextField();
        JTextField txtGolsV = new JTextField();

        Object[] campos = {
                "Partida Encerrada:", comboPartida,
                "Placar REAL - Gols Mandante:", txtGolsM,
                "Placar REAL - Gols Visitante:", txtGolsV
        };

        int op = JOptionPane.showConfirmDialog(parent, campos, "Atualizar Resultado Real", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            try {
                String descEscolhida = (String) comboPartida.getSelectedItem();
                Partida partidaEscolhida = partidas.stream()
                        .filter(p -> p.getDescricao().equals(descEscolhida))
                        .findFirst().orElse(null);

                int golsM = Integer.parseInt(txtGolsM.getText());
                int golsV = Integer.parseInt(txtGolsV.getText());

                partidaEscolhida.registrarResultadoReal(golsM, golsV);

                for (Aposta a : apostas) {
                    if (a.getPartida() == partidaEscolhida) {
                        a.calcularPontuacao();
                    }
                }

                JOptionPane.showMessageDialog(parent, "Resultado registrado! As pontuações foram calculadas.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parent, "Digite apenas números válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void verClassificacao(JFrame parent) {
        List<Participante> parts = new ArrayList<>(grupoPrincipal.getParticipantes());

        if (parts.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Nenhum participante no grupo ainda.");
            return;
        }

        parts.sort(Comparator.comparingInt(Participante::getPontuacaoTotal).reversed());

        StringBuilder sb = new StringBuilder();
        sb.append("=== CLASSIFICAÇÃO: ").append(grupoPrincipal.getNome().toUpperCase()).append(" ===\n\n");

        for (int i = 0; i < parts.size(); i++) {
            Participante p = parts.get(i);
            sb.append((i + 1)).append("º LUGAR - ").append(p.getNome())
                    .append(" | Pontos: ").append(p.getPontuacaoTotal()).append("\n");
        }

        JOptionPane.showMessageDialog(parent, sb.toString(), "Ranking", JOptionPane.INFORMATION_MESSAGE);
    }
}