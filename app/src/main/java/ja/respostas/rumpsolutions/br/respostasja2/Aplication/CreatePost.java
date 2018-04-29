package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.spec.PSSParameterSpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ja.respostas.rumpsolutions.br.respostasja2.R;
import ja.respostas.rumpsolutions.br.respostasja2.funcoes.Funcoes;

public class CreatePost extends AppCompatActivity {

    private Funcoes funcoes = new Funcoes();
    private DatabaseReference databaseReference;
    private MultiAutoCompleteTextView conteudo;
    private TextView usuario;
    private String hora;
    private Spinner materia;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private Usuario user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Postagem postagem = new Postagem();
        mAuth = FirebaseAuth.getInstance();

        conteudo = findViewById(R.id.postPostagem);
        usuario = findViewById(R.id.postUsuario);
        SimpleDateFormat dateFormat_hora = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
        materia = findViewById(R.id.postMateria);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //pegar a hora atual e jogar em uma variável pegue no site do google e noizzzz


        Calendar cal = Calendar.getInstance();


        hora =  cal.get(Calendar.DAY_OF_MONTH)+"/"+
                (cal.get(Calendar.MONTH)+1)+"/"+
                cal.get(Calendar.YEAR)+"-"+
                cal.get(Calendar.HOUR_OF_DAY)+":"+
                cal.get(Calendar.MINUTE);



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        user = new Usuario(this, currentUser);
        userReference = user.getReference();
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario.setText(dataSnapshot.child("nome").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.post_post) {
            writeNewPostagem("1",user.getIdUser(),"Materia Teste","title",conteudo.getText().toString(), hora);
            funcoes.toast(this,"Pergunta postada");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void writeNewPostagem(String uid, String usuario, String materia, String titulo, String conteudo, String hora){


        String key = databaseReference.child("postagens").push().getKey();
        Postagem postagem = new Postagem(usuario, key, materia, hora,conteudo, titulo);


        HashMap<String, String> post = new HashMap<>();
        post.put("usuario"  , postagem.getUsuario()     );
        post.put("conteudo" , postagem.getConteudo()    );
        post.put("uid"      , postagem.getUid()         );
        post.put("materia"  , postagem.getMateria()     );
        post.put("hora"     , postagem.getHora()        );
        post.put("titulo"   , "teste"                   );

        Map<String , Object> childUpdates = new HashMap<>();
        childUpdates.put("/postagens/" + key, post);

        databaseReference.updateChildren(childUpdates);



    }

}
