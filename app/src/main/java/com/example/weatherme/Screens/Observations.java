package com.example.weatherme.Screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.weatherme.Adapters.ObservationAdapter;
import com.example.weatherme.Models.ObservationModel;
import com.example.weatherme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Observations extends AppCompatActivity {

    private Button addObservation;
    private final int responseCode = 1;
    private RecyclerView mRecycleView;
    private ObservationAdapter observationAdapter;
    private ArrayList<ObservationModel> observationModels;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String previousTitleAsId;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == responseCode) {
            if (resultCode == RESULT_OK) {

                Bundle extras = data.getExtras();

                if(extras.getStringArrayList("data") != null){
                    ArrayList<String> newData = extras.getStringArrayList("data");
                    Toast.makeText(this, newData.get(0) + " " + newData.get(1), Toast.LENGTH_SHORT).show();
                    //add to recyclerView
                    ObservationModel observationModel = new ObservationModel(newData.get(1), newData.get(0));
                    observationModels.add(observationModel);
                    observationAdapter.notifyDataSetChanged();

                    //add observation to firebase
                    try {
                        addDataToFirestore(observationModel);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else if (extras.getStringArrayList("data_edit") != null){

                    ArrayList<String> newData = extras.getStringArrayList("data_edit");
                    //add to recyclerView
                    observationModels.get(Integer.parseInt(newData.get(2))).setTitle(newData.get(1));
                    observationModels.get(Integer.parseInt(newData.get(2))).setContent(newData.get(0));
                    observationAdapter.notifyDataSetChanged();

                    try {
                        editDataToFirestore(observationModels.get(Integer.parseInt(newData.get(2))));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }else{
                Toast.makeText(getApplicationContext(), "Observation not added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observations);

        addObservation = findViewById(R.id.add_observation);
        addObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Observations.this, EditObservation.class), responseCode);
            }
        });

        //set recycler View
        mRecycleView = findViewById(R.id.observationsRecyclerView);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        observationModels = new ArrayList<ObservationModel>();
        observationAdapter = new ObservationAdapter(getApplicationContext(), observationModels);
        mRecycleView.setAdapter(observationAdapter);




        //Initialize navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        //Set Home
        bottomNavigationView.setSelectedItemId(R.id.observations);
        //Item Selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), Account.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Retrieve data from firebase on Resume
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("observations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    observationModels.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ObservationModel observationModel = document.toObject(ObservationModel.class);
                        observationModels.add(observationModel);
                    }
                    observationAdapter.notifyDataSetChanged();
                }
            }
        });

        //Set listeners for observations cards
        observationAdapter.setOnItemClickListener(new ObservationAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position) {

                Toast.makeText(getApplicationContext(), observationModels.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                //remove from firestore
                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("observations").document(observationModels.get(position).getTitle()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Observation removed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

                //remove from UI
                observationModels.remove(position);
                observationAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onItemClick(int position) {

                ObservationModel observationModel = observationModels.get(position);
                previousTitleAsId = observationModels.get(position).getTitle();
                //retrieve information to be sent
                ArrayList<String> observationData = new ArrayList<String>();
                observationData.add(observationModel.getTitle());
                observationData.add(observationModel.getContent());
                //to reference position in edit observation screen
                observationData.add(position+""); //string

                //sent data to editObservation screen
                Intent i = new Intent(Observations.this, EditObservation.class);
                i.putExtra("observation",observationData);
                startActivityForResult(i, responseCode);

            }
        });
    }

    private void addDataToFirestore(ObservationModel observationModel) {

        CollectionReference dbObservations = db.collection("users");

        //Convert to Map object
        Map<String, Object> observation = new HashMap<>();
        observation.put("title", observationModel.getTitle());
        observation.put("content", observationModel.getContent());

        //add observation with document id as title -> assuming title's are unique
        dbObservations.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("observations").document(observationModel.getTitle()).set(observation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editDataToFirestore(ObservationModel observationModel) {

        CollectionReference dbObservations = db.collection("users");

        //Convert to Map object
        Map<String, Object> observation = new HashMap<>();
        observation.put("title", observationModel.getTitle());
        observation.put("content", observationModel.getContent());

        //add observation with document id as title -> assuming title's are unique
        dbObservations.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("observations").document(previousTitleAsId).update(observation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
