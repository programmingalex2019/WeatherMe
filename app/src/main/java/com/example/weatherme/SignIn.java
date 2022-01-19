package com.example.weatherme;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextView textLogo;
    private Button signButton;
    private TextView switchSignIn;
    private static boolean signInToggle = false; //False for logIn - True for register

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        //Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //credentials editText
        textInputEmail = findViewById(R.id.emailInput);
        textInputPassword = findViewById(R.id.passwordInput);

        //toggle fields
        textLogo = findViewById(R.id.textLogo);
        signButton = findViewById(R.id.signButton);
        switchSignIn = findViewById(R.id.switchSignIn);

        //toggle between register and login UI
        switchSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signInToggle = !signInToggle;

                if (!signInToggle){
                    textInputEmail.getEditText().setText("");
                    textInputPassword.getEditText().setText("");
                    textLogo.setText(R.string.textLogIn);
                    signButton.setText(R.string.textLogIn);
                    switchSignIn.setText(R.string.register_message);
                }else{
                    textInputEmail.getEditText().setText("");
                    textInputPassword.getEditText().setText("");
                    textLogo.setText(R.string.textRegister);
                    signButton.setText(R.string.textRegister);
                    switchSignIn.setText(R.string.logIn_message);
                }

            }
        });

    }

    //check if user logged in
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //change Activity with
            startActivity(new Intent(getApplicationContext(), Home.class));
        }
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

    public void confirmInput(View v){

        if(!validateEmail() | !validatePassword()){
            return;
        }

        String email = textInputEmail.getEditText().getText().toString().trim();
        String password = textInputPassword.getEditText().getText().toString().trim();

        String input = "Email: " + email;
        input += "\n";
        input += password;

        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();

        // login / register
        if(!signInToggle){
            signInUserWithEmailAndPassword(email, password);
        }else{
            createUserWithEmailAndPassword(email,password);
        }

    }

    private void createUserWithEmailAndPassword(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignUp", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //update UI with User
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }else{
                            // If sign in fails, display a message to the user.
                            Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInUserWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignIn", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //update UI with User
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }else{
                            // If sign in fails, display a message to the user.
                            Log.w("SignIn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
