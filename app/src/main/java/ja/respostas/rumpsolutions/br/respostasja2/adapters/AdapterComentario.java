package ja.respostas.rumpsolutions.br.respostasja2.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import ja.respostas.rumpsolutions.br.respostasja2.Aplication.Usuario;
import ja.respostas.rumpsolutions.br.respostasja2.Objects.Comentario;
import ja.respostas.rumpsolutions.br.respostasja2.R;

public class AdapterComentario extends BaseAdapter {

    private List<Comentario> comentarioList;
    private Activity activity;

    public AdapterComentario(List<Comentario> list, Activity activity){
        this.comentarioList = list;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        try{
            return comentarioList.size();
        }catch (Exception e){
            return 0;
        }

    }

    @Override
    public Object getItem(int i) {
        return comentarioList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View v, ViewGroup viewGroup) {
        View view = activity.getLayoutInflater().inflate(R.layout.adapter_comentarios, viewGroup, false);

        Comentario comentario = comentarioList.get(i);
        Usuario usuario = new Usuario(comentario.getUser());

        final TextView nome = view.findViewById(R.id.comNome);
        final ImageView image = view.findViewById(R.id.comFoto);
        ImageView imageBest = view.findViewById(R.id.comBest);
        TextView comentarioView = view.findViewById(R.id.comComentario);


        comentarioView.setText(comentario.getComentario());

        if (comentario.isBest()){
            imageBest.setVisibility(View.VISIBLE);
        }else{
            imageBest.setVisibility(View.GONE);
        }

        usuario.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("apelido").exists()){
                    nome.setText(dataSnapshot.child("apelido").getValue().toString());
                }else{
                    nome.setText(dataSnapshot.child("nome").getValue().toString());
                }

                try {
                    Glide
                            .with(activity)
                            .load(dataSnapshot.child("foto").getValue().toString())
                            .into(image);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }
}
