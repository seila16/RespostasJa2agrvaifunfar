package ja.respostas.rumpsolutions.br.respostasja2.Aplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ja.respostas.rumpsolutions.br.respostasja2.R;
import ja.respostas.rumpsolutions.br.respostasja2.adapters.AdapterMaterial;
import ja.respostas.rumpsolutions.br.respostasja2.funcoes.Funcoes;


public class MateriaFragment extends Fragment {


    public MateriaFragment() {

    }

    private Funcoes funcoes = new Funcoes();
    private ArrayList<Materias> materiasList;
    private DatabaseReference databaseReference;
    private AdapterMaterial adapaterMatList;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_materia_list, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("materias");
        materiasList = new ArrayList<>();

        ListView listView = view.findViewById(R.id.materiasList);

        adapaterMatList = new AdapterMaterial(materiasList,getActivity());
        listView.setAdapter(adapaterMatList);
        listView.setOnItemClickListener(selectItem());


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                materiasList.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    Materias materias = data.getValue(Materias.class);
                    materiasList.add(materias);
                }
                adapaterMatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

        private AdapterView.OnItemClickListener selectItem(){
            return new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                funcoes.toast(getContext(),materiasList.get(i).getId());
            }
        };



    }





    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        MenuItem item = menu.add("Search");
        item.setIcon(R.drawable.ic_materias);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(getActivity());

        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView = (TextView) sv.findViewById(id);
        textView.setHint("Search location...");
        textView.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(getResources().getColor(R.color.color_text));

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                if(s.length() < 4){
                    Toast.makeText(getActivity(),"sua busca não pode ter menos que 3 caracteres", Toast.LENGTH_SHORT).show();
                    return true;
                }else

                    return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        item.setActionView(sv);
    }*/


}






