package database;

import modelo.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia a conexão com o banco de dados SQLite
 * e todas as operações de persistência do sistema.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:betperfect.db";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Connection connection;

    // =========================================================
    //  CONEXÃO
    // =========================================================

    /** Abre (ou reutiliza) a conexão com o banco SQLite. */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC"); // força o registro do driver
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver SQLite não encontrado. Verifique se sqlite-jdbc.jar está no classpath.", e);
            }
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    /** Fecha a conexão ao encerrar o programa. */
    public static void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

    // =========================================================
    //  CRIAÇÃO DAS TABELAS
    // =========================================================

    /** Cria todas as tabelas se ainda não existirem. */
    public static void inicializarBanco() {
        String sqlCampeonato = """
            CREATE TABLE IF NOT EXISTS campeonato (
                id      INTEGER PRIMARY KEY AUTOINCREMENT,
                nome    TEXT NOT NULL
            )
            """;

        String sqlClube = """
            CREATE TABLE IF NOT EXISTS clube (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                nome            TEXT NOT NULL,
                campeonato_id   INTEGER,
                FOREIGN KEY (campeonato_id) REFERENCES campeonato(id)
            )
            """;

        String sqlParticipante = """
            CREATE TABLE IF NOT EXISTS participante (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                nome            TEXT NOT NULL,
                email           TEXT NOT NULL,
                pontuacao_total INTEGER NOT NULL DEFAULT 0
            )
            """;

        String sqlGrupo = """
            CREATE TABLE IF NOT EXISTS grupo (
                id   INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL
            )
            """;

        String sqlGrupoParticipante = """
            CREATE TABLE IF NOT EXISTS grupo_participante (
                grupo_id        INTEGER,
                participante_id INTEGER,
                PRIMARY KEY (grupo_id, participante_id),
                FOREIGN KEY (grupo_id)        REFERENCES grupo(id),
                FOREIGN KEY (participante_id) REFERENCES participante(id)
            )
            """;

        String sqlPartida = """
            CREATE TABLE IF NOT EXISTS partida (
                id                INTEGER PRIMARY KEY AUTOINCREMENT,
                mandante_id       INTEGER NOT NULL,
                visitante_id      INTEGER NOT NULL,
                data_hora         TEXT    NOT NULL,
                gols_mandante     INTEGER NOT NULL DEFAULT 0,
                gols_visitante    INTEGER NOT NULL DEFAULT 0,
                finalizada        INTEGER NOT NULL DEFAULT 0,
                campeonato_id     INTEGER,
                FOREIGN KEY (mandante_id)   REFERENCES clube(id),
                FOREIGN KEY (visitante_id)  REFERENCES clube(id),
                FOREIGN KEY (campeonato_id) REFERENCES campeonato(id)
            )
            """;

        String sqlAposta = """
            CREATE TABLE IF NOT EXISTS aposta (
                id                      INTEGER PRIMARY KEY AUTOINCREMENT,
                partida_id              INTEGER NOT NULL,
                participante_id         INTEGER NOT NULL,
                gols_mandante_apostado  INTEGER NOT NULL,
                gols_visitante_apostado INTEGER NOT NULL,
                data_hora_aposta        TEXT    NOT NULL,
                FOREIGN KEY (partida_id)      REFERENCES partida(id),
                FOREIGN KEY (participante_id) REFERENCES participante(id)
            )
            """;

        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sqlCampeonato);
            stmt.execute(sqlClube);
            stmt.execute(sqlParticipante);
            stmt.execute(sqlGrupo);
            stmt.execute(sqlGrupoParticipante);
            stmt.execute(sqlPartida);
            stmt.execute(sqlAposta);
            System.out.println("[DB] Banco inicializado com sucesso.");
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao inicializar banco: " + e.getMessage());
        }
    }

    // =========================================================
    //  CAMPEONATO
    // =========================================================

    public static int salvarCampeonato(Campeonato c) {
        String sql = "INSERT INTO campeonato (nome) VALUES (?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNome());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao salvar campeonato: " + e.getMessage());
        }
        return -1;
    }

    /** Retorna o último campeonato salvo, ou null se não houver nenhum. */
    public static Campeonato carregarUltimoCampeonato() {
        String sql = "SELECT id, nome FROM campeonato ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Campeonato c = new Campeonato(rs.getString("nome"));
                c.setId(rs.getInt("id"));
                return c;
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao carregar campeonato: " + e.getMessage());
        }
        return null;
    }

    // =========================================================
    //  CLUBE
    // =========================================================

    public static int salvarClube(Clube clube, int campeonatoId) {
        String sql = "INSERT INTO clube (nome, campeonato_id) VALUES (?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, clube.getNome());
            ps.setInt(2, campeonatoId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao salvar clube: " + e.getMessage());
        }
        return -1;
    }

    public static List<Clube> carregarClubes(int campeonatoId) {
        List<Clube> lista = new ArrayList<>();
        String sql = "SELECT id, nome FROM clube WHERE campeonato_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, campeonatoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Clube c = new Clube(rs.getString("nome"));
                c.setId(rs.getInt("id"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao carregar clubes: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================
    //  PARTICIPANTE
    // =========================================================

    public static int salvarParticipante(Participante p) {
        String sql = "INSERT INTO participante (nome, email, pontuacao_total) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getEmail());
            ps.setInt(3, p.getPontuacaoTotal());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao salvar participante: " + e.getMessage());
        }
        return -1;
    }

    public static void atualizarPontuacaoParticipante(Participante p) {
        String sql = "UPDATE participante SET pontuacao_total = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, p.getPontuacaoTotal());
            ps.setInt(2, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao atualizar pontuação: " + e.getMessage());
        }
    }

    public static List<Participante> carregarParticipantes() {
        List<Participante> lista = new ArrayList<>();
        String sql = "SELECT id, nome, email, pontuacao_total FROM participante";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Participante p = new Participante(rs.getString("nome"), rs.getString("email"));
                p.setId(rs.getInt("id"));
                p.setPontuacaoTotal(rs.getInt("pontuacao_total"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao carregar participantes: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================
    //  GRUPO
    // =========================================================

    public static int salvarGrupo(Grupo g) {
        String sql = "INSERT INTO grupo (nome) VALUES (?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getNome());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao salvar grupo: " + e.getMessage());
        }
        return -1;
    }

    public static void vincularParticipanteAoGrupo(int grupoId, int participanteId) {
        String sql = "INSERT OR IGNORE INTO grupo_participante (grupo_id, participante_id) VALUES (?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, grupoId);
            ps.setInt(2, participanteId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao vincular participante ao grupo: " + e.getMessage());
        }
    }

    public static List<Grupo> carregarGrupos(List<Participante> todosParticipantes) {
        List<Grupo> lista = new ArrayList<>();
        String sqlGrupos = "SELECT id, nome FROM grupo";
        String sqlPartic = "SELECT participante_id FROM grupo_participante WHERE grupo_id = ?";

        try (PreparedStatement psG = getConnection().prepareStatement(sqlGrupos)) {
            ResultSet rsG = psG.executeQuery();
            while (rsG.next()) {
                Grupo g = new Grupo(rsG.getString("nome"));
                g.setId(rsG.getInt("id"));

                try (PreparedStatement psP = getConnection().prepareStatement(sqlPartic)) {
                    psP.setInt(1, g.getId());
                    ResultSet rsP = psP.executeQuery();
                    while (rsP.next()) {
                        int pid = rsP.getInt("participante_id");
                        todosParticipantes.stream()
                                .filter(p -> p.getId() == pid)
                                .findFirst()
                                .ifPresent(p -> {
                                    try { g.adicionarParticipante(p); }
                                    catch (Exception ignored) {}
                                });
                    }
                }
                lista.add(g);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao carregar grupos: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================
    //  PARTIDA
    // =========================================================

    public static int salvarPartida(Partida p, int campeonatoId) {
        String sql = """
            INSERT INTO partida (mandante_id, visitante_id, data_hora, gols_mandante,
                                 gols_visitante, finalizada, campeonato_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getMandante().getId());
            ps.setInt(2, p.getVisitante().getId());
            ps.setString(3, p.getDataHora().format(FMT));
            ps.setInt(4, p.getGolsMandante());
            ps.setInt(5, p.getGolsVisitante());
            ps.setInt(6, p.isFinalizada() ? 1 : 0);
            ps.setInt(7, campeonatoId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao salvar partida: " + e.getMessage());
        }
        return -1;
    }

    public static void atualizarResultadoPartida(Partida p) {
        String sql = """
            UPDATE partida
            SET gols_mandante = ?, gols_visitante = ?, finalizada = 1
            WHERE id = ?
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, p.getGolsMandante());
            ps.setInt(2, p.getGolsVisitante());
            ps.setInt(3, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao atualizar partida: " + e.getMessage());
        }
    }

    public static List<Partida> carregarPartidas(int campeonatoId, List<Clube> clubes) {
        List<Partida> lista = new ArrayList<>();
        String sql = """
            SELECT id, mandante_id, visitante_id, data_hora,
                   gols_mandante, gols_visitante, finalizada
            FROM partida WHERE campeonato_id = ?
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, campeonatoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int midx = rs.getInt("mandante_id");
                int vidx = rs.getInt("visitante_id");

                Clube mandante  = clubes.stream().filter(c -> c.getId() == midx).findFirst().orElse(null);
                Clube visitante = clubes.stream().filter(c -> c.getId() == vidx).findFirst().orElse(null);

                if (mandante == null || visitante == null) continue;

                LocalDateTime dt = LocalDateTime.parse(rs.getString("data_hora"), FMT);
                Partida partida = new Partida(mandante, visitante, dt);
                partida.setId(rs.getInt("id"));

                if (rs.getInt("finalizada") == 1) {
                    partida.registrarResultadoReal(rs.getInt("gols_mandante"), rs.getInt("gols_visitante"));
                }
                lista.add(partida);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao carregar partidas: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================
    //  APOSTA
    // =========================================================

    public static int salvarAposta(Aposta a) {
        String sql = """
            INSERT INTO aposta (partida_id, participante_id,
                                gols_mandante_apostado, gols_visitante_apostado, data_hora_aposta)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getPartida().getId());
            ps.setInt(2, a.getParticipante().getId());
            ps.setInt(3, a.getGolsMandanteApostado());
            ps.setInt(4, a.getGolsVisitanteApostado());
            ps.setString(5, a.getDataHoraAposta().format(FMT));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao salvar aposta: " + e.getMessage());
        }
        return -1;
    }

    public static List<Aposta> carregarApostas(List<Partida> partidas, List<Participante> participantes) {
        List<Aposta> lista = new ArrayList<>();
        String sql = """
            SELECT id, partida_id, participante_id,
                   gols_mandante_apostado, gols_visitante_apostado, data_hora_aposta
            FROM aposta
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int pid  = rs.getInt("partida_id");
                int prid = rs.getInt("participante_id");

                Partida partida = partidas.stream().filter(p -> p.getId() == pid).findFirst().orElse(null);
                Participante part = participantes.stream().filter(p -> p.getId() == prid).findFirst().orElse(null);

                if (partida == null || part == null) continue;

                LocalDateTime dtAposta = LocalDateTime.parse(rs.getString("data_hora_aposta"), FMT);
                try {
                    Aposta a = new Aposta(partida, part,
                            rs.getInt("gols_mandante_apostado"),
                            rs.getInt("gols_visitante_apostado"),
                            dtAposta);
                    a.setId(rs.getInt("id"));
                    lista.add(a);
                } catch (Exception ignored) {
                    // Apostas inválidas carregadas do banco são ignoradas
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao carregar apostas: " + e.getMessage());
        }
        return lista;
    }
}
