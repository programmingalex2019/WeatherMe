package com.example.weatherme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout cityInput;
    private TextView cityCountry;
    private TextView temperature;
    private Button findButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        cityCountry = findViewById(R.id.cityCountry);
        findButton = findViewById(R.id.findCity);

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validateCity()){
                    return;
                }

                cityCountry.setText(cityInput.getEditText().getText().toString());

            }
        });

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

}
