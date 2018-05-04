package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 15973;
    private static final String TAG = "CreatPost";
    private Funcoes funcoes = new Funcoes();
    private DatabaseReference databaseReference;
    private EditText conteudo;
    private EditText url;
    private TextView usuario;
    private String hora;
    private Spinner materia;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private Usuario user;
    private EditText titulo;

    private String caminhoImage;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private byte[] fotoBinario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Postagem postagem = new Postagem();
        mAuth = FirebaseAuth.getInstance();

        conteudo = findViewById(R.id.postPostagem);
        usuario = findViewById(R.id.postUsuario);
        materia = findViewById(R.id.postMateria);
        titulo = findViewById(R.id.postTitle);
        url = findViewById(R.id.postUrl);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();


        hora =  cal.get(Calendar.DAY_OF_MONTH)+"/"+
                (cal.get(Calendar.MONTH)<10 ? "0" : "") + (cal.get(Calendar.MONTH)+1)+"/"+
                cal.get(Calendar.YEAR)+"-"+
                (cal.get(Calendar.HOUR_OF_DAY)<10 ? "0" : "") + cal.get(Calendar.HOUR_OF_DAY)+":"+
                (cal.get(Calendar.MINUTE)<10 ? "0" : "") + cal.get(Calendar.MINUTE);


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
        switch (id){
            case R.id.post_post:
                writeNewPostagem(user.getIdUser(),"teste",titulo.getText().toString(),conteudo.getText().toString(), hora, url.getText().toString(), this.fotoBinario);
                funcoes.toast(this,"Pergunta postada");
                finish();
                break;
            case R.id.post_imagem:
                addImage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addImage() {
        File file = new File(Environment.getExternalStorageDirectory() + "/imagePost.jpg");
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.gc(); // garbage colector
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 3;
                    Bitmap imageBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/imagePost.jpg", options);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    boolean validaCompressao = imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    byte[] imageByte = outputStream.toByteArray();
                    if (validaCompressao) {
                        if (imageByte == null)
                            Toast.makeText(this, "Nenhuma imagem selecionada.", Toast.LENGTH_SHORT).show();
                        else
                            setFotoBinario(imageByte);
                    }else{
                        Toast.makeText(this, "Falha na montagem da imagem.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(this, "Erro ao carregar imagem.",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("Envio de imagem cancelado");
            } else {
                System.out.println("Outra opção de imagem...");
            }
        }
    }

    private void setFotoBinario(byte[] fotoBinario){
        if (fotoBinario != null) {
            this.fotoBinario = fotoBinario;
        }else
            Toast.makeText(this, "Nenhuma imagem selecionada.", Toast.LENGTH_SHORT).show();
    }


    private void writeNewPostagem(String usuario, String materia, String titulo, String conteudo, String hora, String url, byte[] fotoBinario){

        //cria uid da postagem
        String key = databaseReference.child("postagens").push().getKey();

        //enviar imagem do post

        //Configuração do FIREBASE STORAGE para armazenamento de imagem - 01/05/2018
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("posts/"+key+".jpg");

        if (fotoBinario != null) {
            UploadTask uploadTask = storageReference.putBytes(fotoBinario);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreatePost.this, "Falha ao enviar imagem. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    caminhoImage = taskSnapshot.getDownloadUrl().toString();
                }
            });
            Log.d(TAG, "Imagem adicionada");
        }else{
            Log.d(TAG, "Nenhuma imagem adicionada : " + this.fotoBinario);
        }

        //envia dados do post

        Postagem postagem = new Postagem(usuario, key, materia, hora,conteudo, titulo, url);


        HashMap<String, String> post = new HashMap<>();
        post.put("usuario"  , postagem.getUsuario()     );
        post.put("conteudo" , postagem.getConteudo()    );
        post.put("uid"      , postagem.getUid()         );
        post.put("materia"  , postagem.getMateria()     );
        post.put("hora"     , postagem.getHora()        );
        post.put("titulo"   , postagem.getTitulo()      );
        post.put("url"      , postagem.getUrl()         );

        Map<String , Object> childUpdates = new HashMap<>();
        childUpdates.put("/postagens/" + key, post);

        databaseReference.updateChildren(childUpdates);





    }

}
