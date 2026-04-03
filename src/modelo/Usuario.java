package modelo;

public abstract class Usuario {
    protected String nome;
    protected String email;

    public Usuario() {}

    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public abstract void exibirPerfil();

    // Getters e Setters (Encapsulamento)
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}