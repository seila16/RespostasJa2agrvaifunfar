package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

public class Materias {

    public Materias(){

    }

    private String nome;
    private String cor;
    private String grupo;
    private String Id;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNameMateria(String nameMateria) {
        this.nome = nameMateria;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}
