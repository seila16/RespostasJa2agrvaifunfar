package ja.respostas.rumpsolutions.br.respostasja2.Aplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.BaseAdapter;
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
import java.lang.reflect.Array;
import java.security.spec.PSSParameterSpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ja.respostas.rumpsolutions.br.respostasja2.Manifest;
import ja.respostas.rumpsolutions.br.respostasja2.R;
import ja.respostas.rumpsolutions.br.respostasja2.funcoes.Funcoes;

public class CreatePost extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 15973;
    private static final String TAG = "CreatPost";
    private static final int REQUEST_PERMISSION_CODE = 333;
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

    private ArrayList data ;
    private ArrayAdapter arrayAdapter;


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



        data = new ArrayList();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, data);
        criaMaterias();
        materia.setAdapter(arrayAdapter);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
                int k = 0;
                for (int i = 0; i< permissions.length; i++){
                    if (permissions[i].equalsIgnoreCase(Manifest.permission.CAMERA)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        k += 1;
                    }

                    if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        k += 1;
                    }

                    if (permissions[i].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        k += 1;
                    }
                }

                if (k == 3) addImage();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                if (!(titulo.getText().toString().isEmpty())) {
                    if (!(conteudo.getText().toString().isEmpty()) || this.fotoBinario != null) {
                        writeNewPostagem(user.getIdUser(), materia.getSelectedItem().toString(), titulo.getText().toString(), conteudo.getText().toString(), hora, url.getText().toString(), this.fotoBinario);
                        funcoes.toast(this,"Pergunta postada");
                        finish();
                    } else {
                        conteudo.setError("Digite um conteúdo para postagem.");
                    }
                }else{
                    titulo.setError("Campo não pode ficar vazio.");
                }

                break;
            case R.id.post_imagem:
                try{
                    addImage();
                }catch (Exception e){
                    permissions();
                }


                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //abre camera e cria a imagem
    private void addImage() {
        File file = new File(Environment.getExternalStorageDirectory() + "/imagePost.jpg");
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    //recuperar dados da foto tirada
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

    //Seta a foto com array de bytes necessarios
    private void setFotoBinario(byte[] fotoBinario){
        if (fotoBinario != null) {
            this.fotoBinario = fotoBinario;
        }else
            Toast.makeText(this, "Nenhuma imagem selecionada.", Toast.LENGTH_SHORT).show();
    }

    //inicia criação de nova postagem
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

    //cria arraylist com materias
    private ArrayList criaMaterias(){
        DatabaseReference materiaRef = FirebaseDatabase.getInstance().getReference()
                .child("materias");
        materiaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot dataS : dataSnapshot.getChildren()){
                    data.add(dataS.child("nome").getValue().toString());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return null;
    }

    //solicita permissoes para camera
    public void permissions(){
        permissionCamera();
        permissionWrite();
        permissionRead();
    }

    private void permissionCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){

                callDialog("É preciso permitir para abrir a câmera.", new String[]{Manifest.permission.CAMERA});

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA} ,REQUEST_PERMISSION_CODE);
            }

        }else{

        }
    }

    private void permissionWrite() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){

                callDialog("É preciso permitir para a gravação da fotos.", new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,REQUEST_PERMISSION_CODE);
            }

        }else{

        }
    }

    private void permissionRead() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){

                callDialog("É preciso permitir para realizar as a leitura da foto tirada.", new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} ,REQUEST_PERMISSION_CODE);
            }

        }else{

        }
    }

    public void callDialog(String mensagem, final String[] permissions){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões");
        builder.setMessage(mensagem);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(CreatePost.this, permissions, REQUEST_PERMISSION_CODE);
            }
        });
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
