package com.example.weatherme.Screens;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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

public class EditObservation extends AppCompatActivity {

    //UI fields
    private EditText observationContent;
    private EditText observationTitle;
    private Button saveObservation;
    private ImageView goBack;
    //secondary fields
    private boolean editMode = false;
    private String editingPosition;

    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_observation);

        //initialize UI
        observationContent = findViewById(R.id.observation_content);
        observationTitle = findViewById(R.id.observation_title);
        saveObservation = findViewById(R.id.saveObservation);
        goBack = findViewById(R.id.goBack);

        //leave screen
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null){ //if not null -> state is EDIT MODE
            //Receive data to be changed
            ArrayList<String> observationData = extras.getStringArrayList("observation");
            observationTitle.setText(observationData.get(0));
            observationContent.setText(observationData.get(1));
            editingPosition = observationData.get(2);
            editMode = true;
            Log.i("weatherDebug", editingPosition);
        }

        //on save button clicked
        saveObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //title and content need to be not empty
                if(observationContent.getText().toString().trim().length() > 0 && observationTitle.getText().toString().trim().length() > 0){

                    //prepare data to be sent
                    ArrayList<String> observationData = new ArrayList<String>();
                    observationData.add(observationContent.getText().toString().trim()); //0 content
                    observationData.add(observationTitle.getText().toString().trim()); //1 title
                    Intent data = new Intent();

                    //where to retrieve data and in which mode
                    if(!editMode){

                        data.putExtra("data", observationData); //add observation
                        setResult(RESULT_OK, data);
                        finish(); //exit

                    }else{

                        observationData.add(editingPosition);
                        data.putExtra("data_edit", observationData);//add observation
                        setResult(RESULT_OK, data);
                        finish(); //exit

                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Observations should have a title and content", Toast.LENGTH_SHORT).show(); //inform user
                }
            }
        });
    }
}
