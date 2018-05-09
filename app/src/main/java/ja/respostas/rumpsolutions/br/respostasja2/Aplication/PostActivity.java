package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;

import ja.respostas.rumpsolutions.br.respostasja2.R;

public class PostActivity extends AppCompatActivity {

    private TextView viewTitulo;
    private TextView viewConteudo;
    private PhotoView viewImage;
    private TextView viewUrl;
    private String imageURL;

    private String postID;
    private DatabaseReference databaseReference;
    private StorageReference storageRef;
    private DatabaseReference imgReference;
    private ProgressBar progressBarPost;

    private Usuario postador;
    private Postagem postagem;
    private String urlFotoLogado;
    private TextView viewNick;
    private TextView viewMateria;
    private ImageView imageUser;
    private TextView exURl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initElements();

        postID = getIntent().getStringExtra("POST");

        storageRef = FirebaseStorage.getInstance().getReference();
        getImage();

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("postagens")
                .child(postID);
        databaseReference.addListenerForSingleValueEvent(listenerDadosPost());


        imgReference = FirebaseDatabase.getInstance().getReference().child("postagens");


    }


   //findar os negocio do xml
    private void initElements() {
        viewTitulo = findViewById(R.id.exTitulo);
        viewConteudo = findViewById(R.id.exConteudo);
        viewImage = findViewById(R.id.exImage);
        viewUrl = findViewById(R.id.exURL);
        viewMateria = findViewById(R.id.viewMateria);
        viewNick = findViewById(R.id.viewNick);
        imageUser = findViewById(R.id.image_user);
        exURl = findViewById(R.id.exURL);
        progressBarPost = findViewById(R.id.progressBarPost);
    }


    //colocar os dados nas views
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
                    exURl.setText(postagem.getUrl());
                }catch (Exception e){
                    Log.e("Erro", "Erro ao carregar dados", e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    //como colocar imagem dentro do quadrado da tela de post alem de que isso aqui também faz aparecer o nome do cara.
    public void usuarioPostado(){
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

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //carrega nome do usuario que postou
                    if (dataSnapshot.child("apelido").exists()){
                        viewNick.setText(dataSnapshot.child("apelido").getValue().toString());
                    }else{
                        viewNick.setText(dataSnapshot.child("nome").getValue().toString());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            default:break;

        }

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //pegar imagem do storage do google
    public void getImage() {
        try{
            storageRef.child("posts/"+postID+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                   try{
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
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            Log.e("Erro", "Nenhuma imagem recuperada", e);
        }
    }
}
