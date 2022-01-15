package com.example.weatherme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout cityInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);


    }

    private boolean validateCity(){

        String city = cityInput.getEditText().getText().toString().trim();

        if(city.isEmpty()){
            cityInput.setError("Field can't be empty");
            cityInput.setErrorEnabled(true);
            return false;
        }else{
            cityInput.setError(null);
            cityInput.setErrorEnabled(false);
            return true;
        }
    }

    public void findCity(View v){

        if(!validateCity()){
            return;
        }

        String input = "City: " + cityInput.getEditText().getText().toString().trim();

        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }
}
