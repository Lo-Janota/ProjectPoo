package modelo;

public class Administrador extends Usuario {

    public Administrador(String nome, String email) {
        super(nome, email);
    }
    @Override
    public void exibirPerfil() {
        System.out.println("ADMINISTRADOR: " + nome + " | Gerencia o sistema.");
    }
}