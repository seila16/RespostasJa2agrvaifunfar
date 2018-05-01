package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

public class Postagem {

    private String titulo;
    private String conteudo;
    private String hora;
    private String usuario;
    private String materia;
    private String uid;
    private String url;


    public Postagem(){

    }


    public Postagem(String usuario, String uid, String materia, String hora, String conteudo, String titulo, String url){

        this.url = url;
        this.conteudo = conteudo;
        this.materia = materia;
        this.usuario = usuario;
        this.uid = uid;
        this.hora = hora;
        this.titulo = titulo;

    }

    public String getMateria() {
        return this.materia;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getConteudo() {
        return this.conteudo;
    }

    public String getHora() {
        return this.hora;
    }

    public String getUsuario() {
        return this.usuario;
    }

    public String getUid() {
        return this.uid;
    }

    public String getUrl() {
        return this.url;
    }
}
