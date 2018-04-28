package ja.respostas.rumpsolutions.br.respostasja2.adapters;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ja.respostas.rumpsolutions.br.respostasja2.Aplication.Materias;
import ja.respostas.rumpsolutions.br.respostasja2.R;

public class MateriasList extends BaseAdapter {

    private final List<Materias> materiasList;
    private final Activity act;


    public MateriasList(List<Materias> materiasList, Activity act){
        this.materiasList = materiasList;
        this.act = act;
    }





    @Override
    public int getCount() {
        try {
            return materiasList.size();
        } catch (Exception E) {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return materiasList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = act.getLayoutInflater().inflate(R.layout.adaptar_materiaslist,viewGroup,false);

        Materias materia = materiasList.get(i);


        TextView viewLogoMat = view.findViewById(R.id.adapter_logoMat);
            GradientDrawable drawableLogo = (GradientDrawable) viewLogoMat.getBackground();
            drawableLogo.setColor(act.getResources().getColor(R.color.colorPrimary));
            viewLogoMat.setBackground(drawableLogo);

        TextView viewTitle = view.findViewById(R.id.adapter_tituloMat);
            viewLogoMat.setText(materia.getNameMateria().substring(0,1).toUpperCase());

        //return
        return view;



    }
}
