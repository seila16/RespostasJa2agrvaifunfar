package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private EditText titulo;
    private DatabaseReference uid;
    private List<String> nomesM = new ArrayList<String>();
    private String aux;



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
        titulo = findViewById(R.id.postTitle);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        //pegar a hora atual e jogar em uma variável pegue no site do google e noizzzz


        Calendar cal = Calendar.getInstance();


        hora =  cal.get(Calendar.DAY_OF_MONTH)+"/"+
                (cal.get(Calendar.MONTH)+1)+"/"+
                cal.get(Calendar.YEAR)+"-"+
                (cal.get(Calendar.HOUR_OF_DAY)<10 ? "0" : "") + cal.get(Calendar.HOUR_OF_DAY)+":"+
                (cal.get(Calendar.MINUTE)<10 ? "0" : "") + cal.get(Calendar.MINUTE);

        //instaciando a classe materias e pegando os dados dela, em seguida jogando dentro do arraylist criado nomeM.



        //criar um arrayadapter usando um padrao de layout da classe R do android passando o arraylist nomesM
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,nomesM);

        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        materia.setAdapter(spinnerArrayAdapter);

        //metodo do spinner para capturar o item selecionado

        materia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //pega o nome pela posição
                aux = adapterView.getItemAtPosition(i).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/respostas-ja/materias");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Materias mater = dataSnapshot.getValue(Materias.class);
                   while (mater.getNome() != null) {
                       nomesM.add(mater.getNome());
                   }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        user = new Usuario(this, currentUser);
        userReference = user.getReference();
        uid = userReference;
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
            writeNewPostagem(uid.toString(),user.getIdUser(),"teste",titulo.getText().toString(),conteudo.getText().toString(), hora);
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
        post.put("titulo"   , postagem.getTitulo()      );

        Map<String , Object> childUpdates = new HashMap<>();
        childUpdates.put("/postagens/" + key, post);

        databaseReference.updateChildren(childUpdates);



    }

}
