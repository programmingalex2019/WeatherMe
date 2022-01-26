package com.example.weatherme.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weatherme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EditObservation extends AppCompatActivity {

    private EditText observationContent;
    private EditText observationTitle;
    private Button saveObservation;
    private ImageView goBack;

    private boolean editMode = false;
    private boolean existsInDatabase = false;
    private String editingPosition;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_observation);

        observationContent = findViewById(R.id.observation_content);
        observationTitle = findViewById(R.id.observation_title);
        saveObservation = findViewById(R.id.saveObservation);
        goBack = findViewById(R.id.goBack);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        observationTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                existsInDatabase = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            ArrayList<String> observationData = extras.getStringArrayList("observation");
            observationTitle.setText(observationData.get(0));
            observationContent.setText(observationData.get(1));
            editingPosition = observationData.get(2);
            editMode = true;
            Toast.makeText(getApplicationContext(), "Edit mode", Toast.LENGTH_SHORT).show();

        }

        saveObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(observationContent.getText().toString().trim().length() > 0 && observationTitle.getText().toString().trim().length() > 0){

                    //prepare data to be sent
                    ArrayList<String> observationData = new ArrayList<String>();
                    observationData.add(observationContent.getText().toString().trim()); //0 content
                    observationData.add(observationTitle.getText().toString().trim()); //1 title
                    Intent data = new Intent();

                    db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("observations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getData().containsValue(observationTitle.getText().toString().trim())){
                                        Toast.makeText(getApplicationContext(), "An observation with such title already exists", Toast.LENGTH_SHORT).show();
                                        existsInDatabase = true;
                                        break;
                                    }
                                }
                                if(!editMode){
                                    if(!existsInDatabase){
                                        data.putExtra("data", observationData);
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                }else{
                                    if(!existsInDatabase) {
                                        observationData.add(editingPosition);
                                        data.putExtra("data_edit", observationData);
                                        setResult(RESULT_OK, data);
                                        finish();
                                    }
                                }
                            }
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(), "Observations should have a title and content", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
