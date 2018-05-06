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
        listView.setOnItemSelectedListener(selectItem());


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

    private AdapterView.OnItemSelectedListener selectItem() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity().getApplicationContext(), materiasList.get(i).getNome(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

}






