import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {

    // Limite máximo de grupos conforme o enunciado
    private static final int MAX_GRUPOS = 5;

    // Senha fixa do administrador
    private static final String SENHA_ADMIN = "admin123";

    private static List<Clube> clubes = new ArrayList<>();
    private static List<Participante> participantes = new ArrayList<>();
    private static List<Grupo> grupos = new ArrayList<>();
    private static List<Partida> partidas = new ArrayList<>();
    private static List<Aposta> apostas = new ArrayList<>();

    private static Campeonato campeonatoAtual;
    private static Grupo grupoPrincipal;

    public static void main(String[] args) {
        // CORRIGIDO: Campeonato e grupo são cadastrados pelo usuário, não fixos no código
        campeonatoAtual = cadastrarCampeonatoInicial();
        grupoPrincipal = cadastrarGrupoInicial();

        if (campeonatoAtual == null || grupoPrincipal == null) {
            JOptionPane.showMessageDialog(null, "Campeonato ou grupo não criado. Encerrando.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame janela = new JFrame("Sistema de Apostas - " + campeonatoAtual.getNome());
        janela.setSize(480, 480);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        painelPrincipal.setLayout(new GridLayout(9, 1, 10, 10));

        JLabel titulo = new JLabel("BET PERFECT", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        painelPrincipal.add(titulo);

        JButton btn1 = new JButton("1. Cadastrar Clube");
        JButton btn2 = new JButton("2. Cadastrar Participante");
        JButton btn3 = new JButton("3. Cadastrar Partida");
        JButton btn4 = new JButton("4. Fazer uma Aposta");
        JButton btn5 = new JButton("5. Registrar Resultado do Jogo (Admin)");
        JButton btn6 = new JButton("6. Ver Classificação do Grupo");
        JButton btn7 = new JButton("7. Criar Novo Grupo");
        JButton btn0 = new JButton("0. Sair");

        btn1.addActionListener(e -> cadastrarClube(janela));
        btn2.addActionListener(e -> cadastrarParticipante(janela));
        btn3.addActionListener(e -> cadastrarPartida(janela));
        btn4.addActionListener(e -> fazerAposta(janela));
        btn5.addActionListener(e -> registrarResultado(janela));
        btn6.addActionListener(e -> verClassificacao(janela));
        btn7.addActionListener(e -> criarNovoGrupo(janela));
        btn0.addActionListener(e -> System.exit(0));

        painelPrincipal.add(btn1);
        painelPrincipal.add(btn2);
        painelPrincipal.add(btn3);
        painelPrincipal.add(btn4);
        painelPrincipal.add(btn5);
        painelPrincipal.add(btn6);
        painelPrincipal.add(btn7);
        painelPrincipal.add(btn0);

        janela.add(painelPrincipal);
        janela.setVisible(true);
    }

    // --- CADASTRO INICIAL (antes de abrir a janela principal) ---

    private static Campeonato cadastrarCampeonatoInicial() {
        String nome = JOptionPane.showInputDialog(null,
                "Bem-vindo ao BET PERFECT!\nDigite o nome do Campeonato:");
        if (nome == null || nome.trim().isEmpty()) return null;
        return new Campeonato(nome.trim());
    }

    private static Grupo cadastrarGrupoInicial() {
        String nome = JOptionPane.showInputDialog(null,
                "Digite o nome do Grupo principal de apostas:");
        if (nome == null || nome.trim().isEmpty()) return null;
        Grupo g = new Grupo(nome.trim());
        grupos.add(g);
        return g;
    }

    // --- AÇÕES DOS BOTÕES ---

    private static void cadastrarClube(JFrame parent) {
        String nome = JOptionPane.showInputDialog(parent, "Digite o nome do Clube:");
        if (nome != null && !nome.trim().isEmpty()) {
            try {
                Clube c = new Clube(nome.trim());
                campeonatoAtual.adicionarClube(c); // Valida o limite de 8
                clubes.add(c);
                JOptionPane.showMessageDialog(parent,
                        "Clube '" + nome.trim() + "' cadastrado!\nTotal de clubes: " + clubes.size() + "/8");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, ex.getMessage(), "Erro de Regra", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void cadastrarParticipante(JFrame parent) {
        JTextField txtNome = new JTextField();
        JTextField txtEmail = new JTextField();
        Object[] campos = {
                "Nome:", txtNome,
                "Email:", txtEmail
        };

        int op = JOptionPane.showConfirmDialog(parent, campos, "Novo Participante", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            try {
                Participante p = new Participante(txtNome.getText().trim(), txtEmail.getText().trim());
                grupoPrincipal.adicionarParticipante(p); // Valida o limite de 5
                participantes.add(p);
                JOptionPane.showMessageDialog(parent,
                        "Participante '" + p.getNome() + "' cadastrado no grupo '" + grupoPrincipal.getNome() + "'!");
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

        // CORRIGIDO: usuário informa a data/hora real da partida
        JTextField txtDataHora = new JTextField("dd/MM/yyyy HH:mm");

        Object[] campos = {
                "Time Mandante:", comboM,
                "Time Visitante:", comboV,
                "Data e Hora da Partida (dd/MM/yyyy HH:mm):", txtDataHora
        };

        int op = JOptionPane.showConfirmDialog(parent, campos, "Nova Partida", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            int idxM = comboM.getSelectedIndex();
            int idxV = comboV.getSelectedIndex();

            if (idxM == idxV) {
                JOptionPane.showMessageDialog(parent, "Um time não pode jogar contra ele mesmo!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime dataHora = LocalDateTime.parse(txtDataHora.getText().trim(), fmt);

                Partida p = new Partida(clubes.get(idxM), clubes.get(idxV), dataHora);
                campeonatoAtual.registrarPartida(p);
                partidas.add(p);
                JOptionPane.showMessageDialog(parent, "Partida registrada:\n" + p.getDescricao());

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(parent,
                        "Formato de data inválido! Use: dd/MM/yyyy HH:mm", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void fazerAposta(JFrame parent) {
        if (participantes.isEmpty() || partidas.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Cadastre participantes e partidas primeiro!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Partida> abertas = partidas.stream()
                .filter(p -> !p.isFinalizada())
                .toList();

        if (abertas.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Nenhuma partida em aberto para apostar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nomesPart = participantes.stream().map(Participante::getNome).toArray(String[]::new);
        String[] descPartidas = abertas.stream().map(Partida::getDescricao).toArray(String[]::new);

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
                Partida partidaEscolhida = abertas.get(comboPartida.getSelectedIndex());
                int golsM = Integer.parseInt(txtGolsM.getText().trim());
                int golsV = Integer.parseInt(txtGolsV.getText().trim());

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
        // CORRIGIDO: exige autenticação de administrador
        String senha = JOptionPane.showInputDialog(parent, "Digite a senha de Administrador:");
        if (senha == null || !senha.equals(SENHA_ADMIN)) {
            JOptionPane.showMessageDialog(parent, "Senha incorreta! Acesso negado.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Partida> abertas = partidas.stream()
                .filter(p -> !p.isFinalizada())
                .toList();

        if (abertas.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Nenhuma partida aguardando resultado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] descPartidas = abertas.stream().map(Partida::getDescricao).toArray(String[]::new);
        JComboBox<String> comboPartida = new JComboBox<>(descPartidas);
        JTextField txtGolsM = new JTextField();
        JTextField txtGolsV = new JTextField();

        Object[] campos = {
                "Partida Encerrada:", comboPartida,
                "Placar REAL - Gols Mandante:", txtGolsM,
                "Placar REAL - Gols Visitante:", txtGolsV
        };

        int op = JOptionPane.showConfirmDialog(parent, campos, "Atualizar Resultado Real (Admin)", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            try {
                Partida partidaEscolhida = abertas.get(comboPartida.getSelectedIndex());
                int golsM = Integer.parseInt(txtGolsM.getText().trim());
                int golsV = Integer.parseInt(txtGolsV.getText().trim());

                partidaEscolhida.registrarResultadoReal(golsM, golsV);

                // Calcula pontuação de todas as apostas desta partida
                for (Aposta a : apostas) {
                    if (a.getPartida() == partidaEscolhida) {
                        a.calcularPontuacao();
                    }
                }

                JOptionPane.showMessageDialog(parent, "Resultado registrado! Pontuações calculadas.");
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

    // CORRIGIDO: criação de novos grupos com validação do limite de 5
    private static void criarNovoGrupo(JFrame parent) {
        if (grupos.size() >= MAX_GRUPOS) {
            JOptionPane.showMessageDialog(parent,
                    "Limite de " + MAX_GRUPOS + " grupos atingido!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = JOptionPane.showInputDialog(parent, "Nome do novo grupo:");
        if (nome != null && !nome.trim().isEmpty()) {
            Grupo g = new Grupo(nome.trim());
            grupos.add(g);
            JOptionPane.showMessageDialog(parent,
                    "Grupo '" + nome.trim() + "' criado!\nTotal de grupos: " + grupos.size() + "/" + MAX_GRUPOS);
        }
    }
}
