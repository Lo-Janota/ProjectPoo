package modelo;

public abstract class Usuario {
    protected int id;
    protected String nome;
    protected String email;

    public Usuario() {}

    public Usuario(String nome, String email) {
        this.nome  = nome;
        this.email = email;
    }

    public abstract void exibirPerfil();

    public int getId()           { return id; }
    public void setId(int id)    { this.id = id; }

    public String getNome()          { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail()           { return email; }
    public void setEmail(String email) { this.email = email; }
}
