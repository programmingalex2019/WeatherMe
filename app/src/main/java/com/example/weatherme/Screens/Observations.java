package com.example.weatherme.Screens;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    //UI fields and components
    private Button addObservation;
    private RecyclerView mRecycleView;
    private ObservationAdapter observationAdapter;
    private ArrayList<ObservationModel> observationModels;

    //Firebase fields
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Secondary fields
    private final int RESPONSE_CODE = 1; //response code for extras retrieval

    //retrieve observations from firestore
    private void fetchData(){

        //main collection is user as for each registered user there is a specific collection of observation -> RETRIEVE data from there
        db.collection("users")
                .document(FirebaseAuth.getInstance()
                    .getCurrentUser()
                        .getUid())
                            .collection("observations")
                                .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    observationModels.clear(); //avoid bugs

                    for (QueryDocumentSnapshot document : task.getResult()) { //for each document in firestore -> new observationModel
                        ObservationModel observationModel = document.toObject(ObservationModel.class); //automatic serialization
                        observationModels.add(observationModel);
                        Log.i("oDeb", observationModel.toString());
                    }

                    observationAdapter.notifyDataSetChanged(); //update UI
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observations); //assign layout

        //Set recycler View
        mRecycleView = findViewById(R.id.observationsRecyclerView);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        //Bind views
        observationModels = new ArrayList<>();
        observationAdapter = new ObservationAdapter(getApplicationContext(), observationModels);
        mRecycleView.setAdapter(observationAdapter);

        //receive initial data
        fetchData();

        //Set listeners for observations cards //custom changed item click listeners
        observationAdapter.setOnItemClickListener(new ObservationAdapter.OnItemClickListener() {

            @Override
            public void onDeleteClick(int position) {

                //remove from firestore
                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("observations").document(observationModels.get(position).getUID()+"").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Observation removed", Toast.LENGTH_SHORT).show(); //inform user
                        //remove from UI
                        observationModels.remove(position);
                        observationAdapter.notifyItemRemoved(position); //update UI
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show(); //inform user
                    }
                });

            }

            @Override
            public void onItemClick(int position) {

                //entering EDIT MODE
                ObservationModel observationModel = observationModels.get(position);
                //retrieve information to be sent
                ArrayList<String> observationData = new ArrayList<String>();
                observationData.add(observationModel.getTitle());
                observationData.add(observationModel.getContent());
                //to reference position in edit observation screen
                observationData.add(position+""); //string

                //sent data to editObservation screen
                Intent i = new Intent(Observations.this, EditObservation.class);
                i.putExtra("observation",observationData);
                startActivityForResult(i, RESPONSE_CODE); //await edited observation

            }
        });


        //initialize UI
        addObservation = findViewById(R.id.add_observation);
        //when button clicked -> ADD OBSERVATION
        addObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //need to receive added data
                startActivityForResult(new Intent(Observations.this, EditObservation.class), RESPONSE_CODE);
            }
        });

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


        //retrieve data from screen that was pushed on top of this.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //response and key check
        if (requestCode == RESPONSE_CODE) {
            if (resultCode == RESULT_OK) {

                Bundle extras = data.getExtras(); //receive extras

                //Check if extras are coming from ADD_NEW  (observation) MODE
                if(extras.getStringArrayList("data") != null){ //check if data exists

                    ArrayList<String> newData = extras.getStringArrayList("data"); //receive data

                    //add to recyclerView
                    int UID = (int)(Math.random()*(100+1)); //generate ID
                    ObservationModel observationModel = new ObservationModel(newData.get(1), newData.get(0), UID); //1 for title , 0 for content

                    //add observation to firebase and UI
                    try {
                        addDataToFirestore(observationModel);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        fetchData();
                    }


                //Check if extras are coming from EDIT  (observation) MODE
                }else if (extras.getStringArrayList("data_edit") != null){

                    ArrayList<String> newData = extras.getStringArrayList("data_edit"); //receive data
                    //add to recyclerView
                    //1 for title , 0 for content
                    observationModels.get(Integer.parseInt(newData.get(2))).setTitle(newData.get(1)); //INDEX 2 indicates position of the observation being edited
                    observationModels.get(Integer.parseInt(newData.get(2))).setContent(newData.get(0));
                    Log.i("position", newData.get(2));

                    //edit data in firebase
                    try {
                        editDataToFirestore(observationModels.get(Integer.parseInt(newData.get(2))));
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        fetchData();
                    }
                }
            }else{
                Toast.makeText(getApplicationContext(), "Observation not added", Toast.LENGTH_SHORT).show(); //inform user
            }
        }
    }


    private void addDataToFirestore(ObservationModel observationModel) {

        CollectionReference dbObservations = db.collection("users"); //add to specific user

        //Convert to Map object - requirement
        Map<String, Object> observation = new HashMap<>();
        observation.put("UID", observationModel.getUID());
        observation.put("title", observationModel.getTitle());
        observation.put("content", observationModel.getContent());

        //add observation with document id as title -> assuming title's are unique
        dbObservations.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("observations")
                .document(observationModel.getUID()+"").set(observation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show(); //inform user
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show(); //inform user
            }
        });
    }

    private void editDataToFirestore(ObservationModel observationModel) {

        CollectionReference dbObservations = db.collection("users"); //add to specific user

        //Convert to Map object
        Map<String, Object> observation = new HashMap<>();
        observation.put("title", observationModel.getTitle());
        observation.put("content", observationModel.getContent());

        //edit observation with document id as title -> assuming title's are unique
        dbObservations.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("observations").document(observationModel.getUID()+"")
                .update(observation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                observationAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
