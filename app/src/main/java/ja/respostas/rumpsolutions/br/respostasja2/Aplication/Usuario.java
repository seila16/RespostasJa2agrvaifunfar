package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.spec.ECField;

import ja.respostas.rumpsolutions.br.respostasja2.funcoes.Funcoes;

public class Usuario {
    private String idUser;
    private String nome;
    private String email;
    private FirebaseUser currentUser;
    private Context context;

    private DatabaseReference reference;

    private Funcoes funcoes = new Funcoes();


    public Usuario(Context context,FirebaseUser currentUser){
        this.context = context;
        this.idUser = currentUser.getUid();
        this.currentUser = currentUser;

        if(currentUser.getDisplayName() != null){
            if(!(currentUser.getDisplayName().isEmpty())) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(this.idUser)
                        .child("nome")
                        .setValue(currentUser.getDisplayName());
            }
        }

        if (currentUser == null){

            funcoes.abrirActivityUnica(context, LoginActivity2.class);

        }else{
            this.idUser = this.currentUser.getUid();
            this.email = this.currentUser.getEmail();

            connectFirebase(this.idUser);
        }



    }

    public Usuario(String idUser){
        connectFirebase(idUser);
    }

    private void connectFirebase(String idUser) {
        this.reference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(idUser);
    }


    public String getIdUser() {
        return this.idUser;
    }

    public String getEmail() {
        return this.email;
    }

    public String getNome() {
        return this.nome;
    }

    public DatabaseReference getReference() {
        return this.reference;
    }

}
