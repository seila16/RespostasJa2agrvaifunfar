package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ja.respostas.rumpsolutions.br.respostasja2.R;

public class LoginActivity2 extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private Button btn_cadastrar;
    private Button btn_logar;
    private FirebaseAuth mAuth;
    private GoogleApiClient googleApiClient;
    private EditText edit_email;
    private EditText edit_senha;
    private Context context;
    private Usuario usuario;
    private String emailUser;
    private String senhaUser;
    private FirebaseAuth autenticacao;
    private SignInButton singG;
    public static final int SIGN_INC_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        context = this;
        mAuth = FirebaseAuth.getInstance();

        //inicio da configuração do google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        //final da configuração do google

        singG = findViewById(R.id.signG);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        btn_cadastrar = findViewById(R.id.btn_cadastrar2);
        btn_logar = findViewById(R.id.btn_logar2);


        btn_logar.setOnClickListener(realizarLogin());

        btn_cadastrar.setOnClickListener(evtBotaoCadastrar());

        singG.setOnClickListener(evtGoogleLogin());
    }

    @Override
    public void onStart() {
        super.onStart();
        //checar se o usuário já esta logado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            Intent intent = new Intent(context,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    private View.OnClickListener evtBotaoCadastrar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CadastroActivity.class);
                startActivity(intent);
            }
        };
    }


    //REALIZAR LOGIN
    private View.OnClickListener realizarLogin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String email = edit_email.getText().toString();
               String password = edit_senha.getText().toString();
               if ((email!=null) && !email.isEmpty() && (password!=null) && !password.isEmpty()){
                   mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity2.this, new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if (task.isSuccessful()){
                               FirebaseUser user = mAuth.getCurrentUser();
                               updateUI(user);
                           }else{
                               Toast.makeText(context, "Nenhum usuário encontrado.\n Email ou senha inválidos.",Toast.LENGTH_SHORT).show();
                               updateUI(null);
                           }
                   }

                   });
               }else{
                           if(email==null || email.isEmpty()){
                               Toast.makeText(context, "Campo email não digitado.", Toast.LENGTH_SHORT).show();

                           }else if(password==null || password.isEmpty()){
                               Toast.makeText(context, "Campo senha não digitado.", Toast.LENGTH_SHORT).show();

                           }
               }

            }
        };
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // metodo onde pega o resultado do request code que declarei estatico para o login GOOGLE

    protected void onActiveResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == SIGN_INC_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignResult(result);
        }
    }

    //metodo que verifica se o login deu sucesso ou nao no google
    private void handleSignResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            goMainScreen();
        }else{
            Toast.makeText(this,"Não foi possível iniciar a sessão pelo Google+", Toast.LENGTH_SHORT).show();

        }
    }

    private void goMainScreen() {
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //onclick do botao do google
    private View.OnClickListener evtGoogleLogin(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_INC_CODE);
            }
        };
    }
    //FIM DOS METODOS
}
