package ja.respostas.rumpsolutions.br.respostasja2.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ja.respostas.rumpsolutions.br.respostasja2.Aplication.Materias;
import ja.respostas.rumpsolutions.br.respostasja2.Aplication.Postagem;
import ja.respostas.rumpsolutions.br.respostasja2.Aplication.Usuario;
import ja.respostas.rumpsolutions.br.respostasja2.R;

public class AdapterList extends BaseAdapter {

    private final List<Postagem> postagemList;
    private final Activity activity;

    private String nome;


    public AdapterList (List<Postagem> postagemList, Activity activity){
        this.postagemList = postagemList;
        this.activity = activity;
    }



    @Override
    public int getCount() {
        try {
            return postagemList.size();
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return postagemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = activity.getLayoutInflater()
                .inflate(R.layout.adaptar_list, viewGroup, false);

        Postagem postagem = postagemList.get(i);
        final Usuario usuario = new Usuario(postagem.getUsuario());


        //recuperando as view



        TextView viewLogo = view.findViewById(R.id.adapter_logo);
                GradientDrawable drawableLogo = (GradientDrawable) viewLogo.getBackground();
                drawableLogo.setColor( activity.getResources().getColor(R.color.colorPrimary) );
                viewLogo.setBackground(drawableLogo);
        viewLogo.setText(postagem.getMateria().substring(0,1).toUpperCase());

        TextView viewTitulo = view.findViewById(R.id.adapter_titulo);
        viewTitulo.setText(postagem.getTitulo());

        TextView viewConteudo = view.findViewById(R.id.adapter_resumo);
        viewConteudo.setText(postagem.getConteudo());

        //hora
        final TextView viewHora = view.findViewById(R.id.adapter_time);
        try{
            //transforma em tempo
            String date[];
            date = postagem.getHora().split("-");

            String dia[];
            dia = date[0].split("/");

            Calendar hoje = Calendar.getInstance();
            hoje.set(
                    hoje.get(Calendar.YEAR),
                    hoje.get(Calendar.MONTH),
                    hoje.get(Calendar.DAY_OF_MONTH),
                    0,0,0
            );


            Calendar data = Calendar.getInstance();
            data.set(
                    Integer.parseInt(dia[2]),
                    Integer.parseInt(dia[1]),
                    Integer.parseInt(dia[0]),
                    0,0,0
            );
            data.add(Calendar.MONTH, -1);

            Calendar ontem = Calendar.getInstance();
            ontem.set(
                    hoje.get(Calendar.YEAR),
                    hoje.get(Calendar.MONTH),
                    hoje.get(Calendar.DAY_OF_MONTH),
                    0,0,0
            );
            ontem.add(Calendar.DAY_OF_MONTH, -1);


            System.out.println(data.toString());
            System.out.println(hoje.toString());
            System.out.println("-----------------------------------------------------------------------");

            if (data.before(ontem)){
                viewHora.setText(date[0]);
            }else if(data.before(hoje)){
                viewHora.setText("Ontem " + date[1]);
            }else if(data.after(ontem)){
                viewHora.setText("Hoje " + date[1]);
            }

        }catch (Exception e){
            viewHora.setText(postagem.getHora());
        }






        //Nome usuario
        final TextView viewUsuario = view.findViewById(R.id.adapter_usuario);
        usuario.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewUsuario.setText(dataSnapshot.child("nome").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        return view;
    }
}
