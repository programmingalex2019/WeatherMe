package com.example.weatherme;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class SignIn extends AppCompatActivity {

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextView textLogo;
    private Button signButton;
    private TextView switchSignIn;
    private static boolean signInToggle = false; //False for logIn - True for register

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        textInputEmail = findViewById(R.id.emailInput);
        textInputPassword = findViewById(R.id.passwordInput);

        textLogo = findViewById(R.id.textLogo);
        signButton = findViewById(R.id.signButton);

        switchSignIn = findViewById(R.id.switchSignIn);

        switchSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signInToggle = !signInToggle;

                if (!signInToggle){
                    textLogo.setText(R.string.textLogIn);
                    signButton.setText(R.string.textLogIn);
                    switchSignIn.setText(R.string.register_message);
                }else{
                    textLogo.setText(R.string.textRegister);
                    signButton.setText(R.string.textRegister);
                    switchSignIn.setText(R.string.logIn_message);
                }

            }
        });


    }


    private boolean validateEmail(){
        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(emailInput.isEmpty()){
            textInputEmail.setError("Field can't be empty");
            return false;
        }
        else if(!emailInput.matches(emailPattern)){
            textInputEmail.setError("Must be valid email");
            return false;
        }
        else{
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(){
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if(passwordInput.isEmpty()){
            textInputPassword.setError("Field can't be empty");
            return false;
        }else if(passwordInput.length() <= 6){
            textInputPassword.setError("6 or more characters required");
            return false;
        }
        else{
            textInputPassword.setError(null);
            return true;
        }
    }

    private void confirmInput(View v){

        if(!validateEmail() | !validatePassword()){
            return;
        }

        String input = "Email: " + textInputEmail.getEditText().getText().toString().trim();
        input += "\n";
        input += textInputPassword.getEditText().getText().toString().trim();

        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();

    }
}
