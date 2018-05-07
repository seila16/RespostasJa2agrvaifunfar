package ja.respostas.rumpsolutions.br.respostasja2.Aplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ja.respostas.rumpsolutions.br.respostasja2.R;
import ja.respostas.rumpsolutions.br.respostasja2.adapters.AdapterList;
import ja.respostas.rumpsolutions.br.respostasja2.funcoes.Funcoes;

public class ListFragment extends Fragment {

    private Funcoes funcoes = new Funcoes();
    private DatabaseReference reference;
    private ArrayList<Postagem> postagens;
    private AdapterList adapterList;
    private ProgressBar progressBar;

    private String filtroMateria;

    public ListFragment() {


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        final ProgressBar progressBar = view.findViewById(R.id.progB);
        reference = FirebaseDatabase.getInstance().getReference().child("postagens");
        postagens = new ArrayList<>();
        try {
            Bundle filtroBundle = getArguments();
            filtroMateria = filtroBundle.getString("MATERIA");
            Toast.makeText(getActivity(), filtroMateria, Toast.LENGTH_SHORT).show();
        }catch (Exception e){

        }


        ListView listView = view.findViewById(R.id.listPostagens);
        adapterList = new AdapterList(postagens, getActivity());
        listView.setAdapter(adapterList);
        listView.setOnItemClickListener(actionSelectItem());



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postagens.clear();
                progressBar.setVisibility(View.VISIBLE);
                for (DataSnapshot data : dataSnapshot.getChildren()){

                    Postagem postagem = data.getValue(Postagem.class);
                    postagens.add(postagem);

                }
                Collections.reverse(postagens);
                adapterList.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;

    }

    private AdapterView.OnItemClickListener actionSelectItem() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //funcoes.toast(getContext(), postagens.get(i).getUid());
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("POST", postagens.get(i).getUid());
                startActivity(intent);
            }
        };
    }


}
