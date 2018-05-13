package ja.respostas.rumpsolutions.br.respostasja2.Objects;

public class Comentario {

    private String user;
    private String comentario;
    private boolean best;
    private String key;


    public String getComentario() {
        return comentario;
    }

    public boolean isBest() {
        return best;
    }

    public String getUser() {
        return user;
    }

    public String getKey() { return this.key; }
}
