package modelo;

public class Clube {
    private int id;
    private String nome;

    public Clube() {}

    public Clube(String nome) {
        this.nome = nome;
    }

    public int getId()           { return id; }
    public void setId(int id)    { this.id = id; }

    public String getNome()          { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
