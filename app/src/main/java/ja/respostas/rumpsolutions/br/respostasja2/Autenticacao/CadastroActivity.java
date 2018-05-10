package ja.respostas.rumpsolutions.br.respostasja2.Autenticacao;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ja.respostas.rumpsolutions.br.respostasja2.Aplication.LoginActivity2;
import ja.respostas.rumpsolutions.br.respostasja2.Aplication.MainActivity;
import ja.respostas.rumpsolutions.br.respostasja2.R;

public class CadastroActivity extends AppCompatActivity {

    private EditText cadastro_nome;
    private EditText cadastro_email;
    private EditText cadastro_senha;
    private CheckBox chBox;
    private Button cadastro_concluir;
    private AlertDialog alerta;
    private TextView cadastro_cancelar;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        initiElements();
        chBox = findViewById(R.id.check_Confirm);
        chBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checarBox();
            }
        });
        cadastro_concluir.setOnClickListener(actionConcluir());
        cadastro_cancelar.setOnClickListener(actionCancelar());

    }

    private View.OnClickListener actionCancelar() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        };
    }


   private void alertar(){
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle("Termos de uso e compromisso");
       builder.setMessage("Deseja ler os termos de uso e compromisso ou somente aceitar?");
       builder.setPositiveButton("Ler", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               //aqui a gente vai jogar ele pra activity do termo de uso.
               Toast.makeText(CadastroActivity.this,"Indo ler o texto",Toast.LENGTH_SHORT).show();
           }
       });

       builder.setNegativeButton("Somente aceitar", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               Toast.makeText(CadastroActivity.this,"Termos aceitos",Toast.LENGTH_SHORT).show();
           }
       });

       AlertDialog alert = builder.create();
       alert.show();
   }


   private void checarBox() {
       if (chBox.isChecked()) {
           alertar();
       }
   }

    //função botao concluir
    private View.OnClickListener actionConcluir() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = cadastro_email.getText().toString();
                String password = cadastro_senha.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                } else {
                                    Toast.makeText(CadastroActivity.this, "Email/Senha inválido, tente novamente!", Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            }
        };
    }

    //quando concluir o cadastro
    private void updateUI(FirebaseUser user) {

        if (user != null) {
            String nome = cadastro_nome.getText().toString();
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(user.getUid())
                    .child("nome")
                    .setValue(nome);

            Intent intent = new Intent(this, LoginActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    //inicia instancia dos elementos
    private void initiElements(){
        cadastro_nome = findViewById(R.id.cadastro_nome);
        cadastro_email = findViewById(R.id.cadastro_email);
        cadastro_senha = findViewById(R.id.cadastro_senha);
        cadastro_concluir = findViewById(R.id.cadastro_concluir);
        cadastro_cancelar = findViewById(R.id.cadastro_cancelar);

        mAuth = FirebaseAuth.getInstance();

    }


}
