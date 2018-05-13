package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ja.respostas.rumpsolutions.br.respostasja2.Objects.Comentario;
import ja.respostas.rumpsolutions.br.respostasja2.R;
import ja.respostas.rumpsolutions.br.respostasja2.adapters.AdapterComentario;
import ja.respostas.rumpsolutions.br.respostasja2.funcoes.Funcoes;

public class PostActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView viewTitulo;
    private TextView viewConteudo;
    private PhotoView viewImage;
    private TextView viewUrl;
    private String imageURL;

    private Funcoes funcoes = new Funcoes();

    private String postID;
    private DatabaseReference databaseReference;
    private StorageReference storageRef;
    private ProgressBar progressBarPost;

    private Postagem postagem;
    private String urlFotoLogado;
    private TextView viewNick;
    private TextView viewMateria;
    private ImageView imageUser;

    private EditText editTextComentario;
    private Button buttonComentario;
    private ExpandableHeightListView listComentario;

    private Usuario usuarioLogado;
    private TextView logadoNome;
    private ImageView logadoFoto;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private GoogleApiClient googleApiClient;

    private ArrayList<Comentario> comentarioArrayList;
    private AdapterComentario adapterComent;
    private DatabaseReference comentariosRef;

    private static final String TAG = "PostActivity";
    private DatabaseReference melhorComentariosRef;
    private ArrayList<Comentario> bestArrayList;
    private AdapterComentario adapterComentBest;
    private ExpandableHeightListView listComentarioBest;
    private Usuario usuarioPost;

    private String ID_USUARIOLOGADO;
    private String ID_USUARIOPOST;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initElements();

        comentarioArrayList = new ArrayList<>();
        adapterComent = new AdapterComentario(comentarioArrayList, this);
        listComentario.setExpanded(true);
        listComentario.setAdapter(adapterComent);
        listComentario.setOnItemClickListener(onItemClickListener_comentarios());



        bestArrayList = new ArrayList<>();
        adapterComentBest = new AdapterComentario(bestArrayList, this);
        listComentarioBest.setExpanded(true);
        listComentarioBest.setAdapter(adapterComentBest);
        listComentarioBest.setOnItemClickListener(onItemClickListener_comentariosBest());


        //recupera a postagem clicada
        postID = getIntent().getStringExtra("POST");

        //Login silencioso
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        //autentica com firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){

                    usuarioLogado = new Usuario(user.getUid());
                    usuarioLogado.getReference().addValueEventListener(recuperaDadosLogado());
                    ID_USUARIOLOGADO = user.getUid();


                }else{

                    goLoginScreen();

                }
            }
        };



        //inicia a recuperação da imagem
        storageRef = FirebaseStorage.getInstance().getReference();
        getImage();

        //recupera dados da postagem
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("postagens")
                .child(postID);
        databaseReference.addValueEventListener(listenerDadosPost());



        comentariosRef = databaseReference.child("comentarios");
        atualizaLista();

        melhorComentariosRef = databaseReference.child("best");
        atualizaListaBest();

        //Listeners
        buttonComentario.setOnClickListener(onClickListener_btnComentario());



    }

    private AdapterView.OnItemClickListener onItemClickListener_comentariosBest() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,int i, long l) {

                final int j = i;
                if (ID_USUARIOPOST.compareTo(ID_USUARIOLOGADO) == 0){

                    AlertDialog.Builder builder = funcoes.createBuilder(PostActivity.this, "Melhor resposta", "Gostaria de desmarcar a resposta de MELHOR RESPOSTA.");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int inter) {

                            marcarComoRespostaSimples(j);
                        }
                    });
                    builder.setNegativeButton("Não", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (ID_USUARIOLOGADO.compareTo(bestArrayList.get(i).getUser()) == 0){

                    AlertDialog.Builder builder = funcoes.createBuilder(PostActivity.this, "Deletar comentário", "Deseja apagar seu comentário?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int inter) {

                            melhorComentariosRef.child(bestArrayList.get(j).getKey()).setValue(null);
                        }
                    });
                    builder.setNegativeButton("Não", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }


            }
        };
    }

    private void marcarComoRespostaSimples(int i) {
        try{

            String key = bestArrayList.get(i).getKey();

            HashMap<String, Object> map = new HashMap<>();
            map.put("key"       , key);
            map.put("comentario", bestArrayList.get(i).getComentario() );
            map.put("user"      , bestArrayList.get(i).getUser() );
            map.put("best"      , false );


            comentariosRef.child(key).setValue(map);

            melhorComentariosRef.child(key).setValue(null);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener_comentarios() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,int i, long l) {

                final int j = i;
                Log.i(TAG, "onItemClick: "+comentarioArrayList.get(i).getUser());

                if (ID_USUARIOLOGADO.compareTo(ID_USUARIOPOST) == 0){
                    AlertDialog.Builder builder = funcoes.createBuilder(PostActivity.this, "Melhor resposta", "Gostaria de marcar a resposta como MELHOR RESPOSTA.");

                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int inter) {

                            marcarComoMelhorResposta(j);
                        }
                    });
                    builder.setNegativeButton("Não", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else if (ID_USUARIOLOGADO.compareTo(comentarioArrayList.get(i).getUser()) == 0){

                    AlertDialog.Builder builder = funcoes.createBuilder(PostActivity.this, "Apagar mensagem", "Deseja apagar esta mensagem?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            comentariosRef.child(comentarioArrayList.get(j).getKey()).setValue(null);
                        }
                    });
                    builder.setNegativeButton("Não", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }


            }
        };
    }


    private void atualizaListaBest() {
        try {

            melhorComentariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    bestArrayList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        Comentario comentario = data.getValue(Comentario.class);
                        bestArrayList.add(comentario);

                    }
                    Collections.reverse(bestArrayList);
                    adapterComentBest.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void atualizaLista() {

        try {
            comentariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comentarioArrayList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        Comentario comentario = data.getValue(Comentario.class);
                        comentarioArrayList.add(comentario);

                    }
                    Collections.reverse(comentarioArrayList);
                    adapterComent.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private View.OnClickListener onClickListener_btnComentario() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextComentario.getText().toString().isEmpty()){
                    editTextComentario.setError("Campo não pode ficar vazio!");
                }else{
                    String keyComentario = comentariosRef.push().getKey();

                    HashMap<String, Object> sendComentario = new HashMap<>();
                    sendComentario.put("user", ID_USUARIOLOGADO);
                    sendComentario.put("comentario", editTextComentario.getText().toString());
                    sendComentario.put("best", false);
                    sendComentario.put("key", keyComentario);

                    comentariosRef.child(keyComentario).setValue(sendComentario);
                    editTextComentario.setText("");
                    Toast.makeText(PostActivity.this, "Agradecemos sua resposta.", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //findar os negocio do xml
    private void initElements() {
        viewTitulo      = findViewById(R.id.exTitulo);
        viewConteudo    = findViewById(R.id.exConteudo);
        viewImage       = findViewById(R.id.exImage);
        viewUrl         = findViewById(R.id.exURL);
        viewMateria     = findViewById(R.id.viewMateria);
        viewNick        = findViewById(R.id.viewNick);
        imageUser       = findViewById(R.id.image_user);
        progressBarPost = findViewById(R.id.progressBarPost);

        logadoFoto = findViewById(R.id.ulFoto);
        logadoNome = findViewById(R.id.ulNome);

        listComentario = findViewById(R.id.ul_list_comentario);
        listComentarioBest = findViewById(R.id.ul_list_melhor_comentario);

        editTextComentario  = findViewById(R.id.ul_edt_comentario);
        buttonComentario    = findViewById(R.id.ul_btn_comentario);
   }

    // // Inicia Cabeçalho

    //como colocar imagem dentro do quadrado da tela de post
    // alem de que isso aqui também faz aparecer o nome do cara.
    public void usuarioPostado(String id) {

        usuarioPost = new Usuario(id);
        usuarioPost.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //carrega image da postagem
                    usuarioPost = dataSnapshot.getValue(Usuario.class);

                    try {
                        urlFotoLogado = dataSnapshot.child("foto").getValue().toString();
                        Glide.with(PostActivity.this)
                                .load(urlFotoLogado)
                                .into(imageUser);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //carrega nome do usuario que postou
                    if (dataSnapshot.child("apelido").exists()) {
                        viewNick.setText(dataSnapshot.child("apelido").getValue().toString());
                    } else {
                        viewNick.setText(dataSnapshot.child("nome").getValue().toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ID_USUARIOPOST = id;

    }

    // // Finaliza Cabeçado

    // // Inicia Postagem

    //Preencher campos de dados da postagem
    private ValueEventListener listenerDadosPost() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postagem = dataSnapshot.getValue(Postagem.class);

                try {
                    viewTitulo.setText(postagem.getTitulo());
                    viewConteudo.setText(postagem.getConteudo());
                    viewUrl.setText(postagem.getUrl());
                    viewMateria.setText(postagem.getMateria());
                } catch (Exception e) {
                    Log.e("Erro", "Erro ao carregar dados", e);
                }

                //recupera usuario que postou
                usuarioPostado(postagem.getUsuario());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    //pegar imagem do storage do google
    public void getImage() {
        try {
            storageRef.child("posts/" + postID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        progressBarPost.setVisibility(View.VISIBLE);
                        imageURL = uri.toString();
                        Glide
                                .with(PostActivity.this)
                                .load(imageURL)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressBarPost.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(viewImage);
                        viewImage.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Erro", "Nenhuma imagem recuperada", e);
        }
    }

    // // Finaliza Postagem

    // // Inicia Comentario

    //preenche campos
    private ValueEventListener recuperaDadosLogado(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarioLogado = dataSnapshot.getValue(Usuario.class);

                try {
                    if (dataSnapshot.child("apelido").exists()) {
                        logadoNome.setText(usuarioLogado.getApelido());
                    } else {
                        logadoNome.setText(usuarioLogado.getNome());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                try{

                    Glide
                            .with(PostActivity.this)
                            .load(dataSnapshot.child("foto").getValue())
                            .into(logadoFoto);

                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


    }


    private void marcarComoMelhorResposta(int i) {
        try{

            String key = comentarioArrayList.get(i).getKey();

            HashMap<String, Object> map = new HashMap<>();
            map.put("key"       , key);
            map.put("comentario", comentarioArrayList.get(i).getComentario() );
            map.put("user"      , comentarioArrayList.get(i).getUser() );
            map.put("best"      , true );


            melhorComentariosRef.child(key).setValue(map);

            comentariosRef.child(key).setValue(null);


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // // Finaliza Comentario

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;

        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null){
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }

}
