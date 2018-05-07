package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import ja.respostas.rumpsolutions.br.respostasja2.Autenticacao.CadastroActivity;
import ja.respostas.rumpsolutions.br.respostasja2.R;
import ja.respostas.rumpsolutions.br.respostasja2.funcoes.Funcoes;

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
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private Funcoes funcoes = new Funcoes();
    private ProgressBar progressBar;

    private final String TAG = "Login Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        context = this;
        mAuth = FirebaseAuth.getInstance();

        //inicio da configuração do google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        //final da configuração do google

        singG = findViewById(R.id.signG);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        btn_cadastrar = findViewById(R.id.btn_cadastrar2);
        btn_logar = findViewById(R.id.btn_logar2);
        progressBar = findViewById(R.id.progressBar);


        btn_logar.setOnClickListener(realizarLogin());

        btn_cadastrar.setOnClickListener(evtBotaoCadastrar());

        singG.setOnClickListener(evtGoogleLogin());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    goMainScreen();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        //checar se o usuário já esta logado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    private void updateUI(FirebaseUser currentUser){
        if(currentUser != null){
            funcoes.abrirActivityUnica(this, MainActivity.class);
        }else{
            Log.w(TAG, " Não foi possível realizar o login ");
        }
    }


    private View.OnClickListener evtBotaoCadastrar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcoes.abrirActivityNova(LoginActivity2.this, CadastroActivity.class);
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
                if ( (email!=null) && !(email.isEmpty()) && (password!=null) && !(password.isEmpty()) ){
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == SIGN_INC_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignResult(result);
        }
    }

    //metodo que verifica se o login deu sucesso ou nao no google
    private void handleSignResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            firebaseAuthWithGoogle(result.getSignInAccount());
        }else{
            Toast.makeText(this,"Não foi possível iniciar a sessão pelo Google+", Toast.LENGTH_SHORT).show();

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {
        progressBar.setVisibility(View.VISIBLE);
        singG.setVisibility(View.GONE);

        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    singG.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void goMainScreen() {
        funcoes.abrirActivityUnica(this, MainActivity.class);

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

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuthListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

    //FIM DOS METODOS
}
