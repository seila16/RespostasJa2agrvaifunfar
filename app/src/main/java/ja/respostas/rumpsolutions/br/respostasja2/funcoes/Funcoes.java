package ja.respostas.rumpsolutions.br.respostasja2.funcoes;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ja.respostas.rumpsolutions.br.respostasja2.Aplication.LoginActivity2;

public class Funcoes {


    //Limpa todas as activities e inicia somente uma nova chamada aqui
    public void abrirActivityUnica(Context context, Class c){
        Intent intent = new Intent(context, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //Inicia uma nova activity
    public void abrirActivityNova(Context context, Class c) {
        Intent intent = new Intent(context, c);
        context.startActivity(intent);
    }

    //função para facilitar implementação do TOAST
    public void toast(Context context, String s){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    //sobrecarga do metodo TOAST...
    //int duração = Toast.Length_short / Toast.Length_LONG
    private void toast(Context context, String s, int duracao){
        Toast.makeText(context, s, duracao).show();
    }
}
