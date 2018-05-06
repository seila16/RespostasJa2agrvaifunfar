package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

    private Usuario postador;
    private Postagem postagem;

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



    }

    private void initElements() {
        viewTitulo = findViewById(R.id.exTitulo);
        viewConteudo = findViewById(R.id.exConteudo);
        viewImage = findViewById(R.id.exImage);
        viewUrl = findViewById(R.id.exURL);
    }



    private ValueEventListener listenerDadosPost() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postagem = dataSnapshot.getValue(Postagem.class);

                try {
                    viewTitulo.setText(postagem.getTitulo());
                    viewConteudo.setText(postagem.getConteudo());
                    viewUrl.setText(postagem.getUrl());
                }catch (Exception e){
                    Log.e("Erro", "Erro ao carregar dados", e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
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

    public void getImage() {
        try{
            storageRef.child("posts/"+postID+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    try{
                        imageURL = uri.toString();
                        Glide.with(PostActivity.this).load(imageURL).into(viewImage);
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
