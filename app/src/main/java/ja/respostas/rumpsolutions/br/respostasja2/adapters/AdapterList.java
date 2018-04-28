package ja.respostas.rumpsolutions.br.respostasja2.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ja.respostas.rumpsolutions.br.respostasja2.Aplication.Postagem;
import ja.respostas.rumpsolutions.br.respostasja2.R;

public class AdapterList extends BaseAdapter {

    private final List<Postagem> postagemList;
    private final Activity activity;

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

        TextView viewHora = view.findViewById(R.id.adapter_time);
        viewHora.setText(postagem.getHora());

        return view;
    }
}
