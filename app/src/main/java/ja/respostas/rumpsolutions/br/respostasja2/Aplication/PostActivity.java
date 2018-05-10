package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class PostActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView viewTitulo;
    private TextView viewConteudo;
    private PhotoView viewImage;
    private TextView viewUrl;
    private String imageURL;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //recupera a postagem clicada
        postID = getIntent().getStringExtra("POST");
        comentarioArrayList = new ArrayList<>();

        //recupera comentarios da postagem
        comentariosRef = FirebaseDatabase.getInstance().getReference()
                .child("postagens")
                .child(postID)
                .child("comentarios");

        comentariosRef.addValueEventListener(listenerComentarios());



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initElements();



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

                    usuarioLogado = new Usuario(PostActivity.this, user);
                    preencheComentarios();

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

        //seta adapter nos comentarios
        adapterComent = new AdapterComentario(comentarioArrayList, PostActivity.this);
        listComentario.setAdapter(adapterComent);
        listComentario.setExpanded(true);
        listComentario.setOnItemClickListener(listenerItemComentario());


        //seta botao de enviar comentario
        buttonComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextComentario.getText().toString().isEmpty()){
                    editTextComentario.setError("Campo não pode ficar vazio!");
                }else{
                    String keyComentario = comentariosRef.push().getKey();

                    HashMap<String, Object> sendComentario = new HashMap<>();
                    sendComentario.put("user", usuarioLogado.getIdUser());
                    sendComentario.put("comentario", editTextComentario.getText().toString());
                    sendComentario.put("best", false);

                    comentariosRef.child(keyComentario).setValue(sendComentario);
                    editTextComentario.setText("");
                    Toast.makeText(PostActivity.this, "Agradecemos sua resposta.", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        editTextComentario  = findViewById(R.id.ul_edt_comentario);
        buttonComentario    = findViewById(R.id.ul_btn_comentario);
   }

    // // Inicia Cabeçalho

    //como colocar imagem dentro do quadrado da tela de post
    // alem de que isso aqui também faz aparecer o nome do cara.
    public void usuarioPostado() {
        Usuario usuarioPost = new Usuario(postagem.getUsuario());
        usuarioPost.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //carrega image da postagem
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
                    usuarioPostado();
                    viewMateria.setText(postagem.getMateria());
                } catch (Exception e) {
                    Log.e("Erro", "Erro ao carregar dados", e);
                }
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

    // //Inicia Comentario

    //preenche campos
    private void preencheComentarios(){

        usuarioLogado.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    if (dataSnapshot.child("apelido").exists()) {
                        logadoNome.setText(dataSnapshot.child("apelido").getValue().toString());
                    } else {
                        logadoNome.setText(dataSnapshot.child("nome").getValue().toString());
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
        });


    }

    private AdapterView.OnItemClickListener listenerItemComentario() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(PostActivity.this, comentarioArrayList.get(i).getComentario(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private ValueEventListener listenerComentarios() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comentarioArrayList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Comentario comentario = data.getValue(Comentario.class);
                    comentarioArrayList.add(comentario);
                }
                Collections.reverse(comentarioArrayList);
                adapterComent.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
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
