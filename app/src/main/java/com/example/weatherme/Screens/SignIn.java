package com.example.weatherme.Screens;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weatherme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    //Fields
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextView textLogo;
    private Button signButton;
    private TextView switchSignIn;
    private boolean signInToggle;
    //Firebase
    private FirebaseAuth _mAuth; //for registration and signIn
    private FirebaseFirestore _db = FirebaseFirestore.getInstance(); //firestore database

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        //Initialize Firebase Auth
        _mAuth = FirebaseAuth.getInstance();

        signInToggle = false;//False for logIn - True for register

        //credentials editText
        textInputEmail = findViewById(R.id.emailInput);
        textInputPassword = findViewById(R.id.passwordInput);

        //toggle fields
        textLogo = findViewById(R.id.textLogo);
        signButton = findViewById(R.id.signButton);
        switchSignIn = findViewById(R.id.switchSignIn);

        //toggle between register and login
        switchSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signInToggle = !signInToggle; //toggle

                if (!signInToggle){ //LogIn
                    textInputEmail.getEditText().setText("");
                    textInputPassword.getEditText().setText("");
                    textLogo.setText(R.string.textLogIn);
                    signButton.setText(R.string.textLogIn);
                    switchSignIn.setText(R.string.register_message);
                }else{ //Register
                    textInputEmail.getEditText().setText("");
                    textInputPassword.getEditText().setText("");
                    textLogo.setText(R.string.textRegister);
                    signButton.setText(R.string.textRegister);
                    switchSignIn.setText(R.string.logIn_message);
                }
            }
        });
    }

    //check if user logged in -> avoid typing user credentials -> check cache
    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = _mAuth.getCurrentUser();
        if(currentUser != null){
            //change Activity with
            startActivity(new Intent(getApplicationContext(), Home.class));
        }
    }

    //input validation for email -> RESTRICTION: LOW
    private boolean validateEmail(){
        String emailInput = textInputEmail.getEditText().getText().toString().trim(); //access current editText text field for email
        if(emailInput.isEmpty()){
            textInputEmail.setError("Field can't be empty");
            return false; //failed validation
        }
        else if(!emailInput.contains("@")){ //emails should contain @
            textInputEmail.setError("Must be valid email");
            return false;
        }
        else{
            textInputEmail.setError(null); //no error
            return true; //passed validation
        }
    }

    //input validation for password -> RESTRICTION: LOW
    private boolean validatePassword(){
        String passwordInput = textInputPassword.getEditText().getText().toString().trim(); //access current editText text field for password

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

    //happens when sign/register button clicked
    public void confirmInput(View v){

        // '|' used to check both input fields
        if(!validateEmail() | !validatePassword()){
            return;
        }

        //preparation after passed validation -> get email and password values
        String email = textInputEmail.getEditText().getText().toString().trim();
        String password = textInputPassword.getEditText().getText().toString().trim();

        // login / register -> check which method to call depending on toggle
        if(!signInToggle){
            signInUserWithEmailAndPassword(email, password);
        }else{
            createUserWithEmailAndPassword(email,password);
        }
    }

    //create user in firebase
    private void createUserWithEmailAndPassword(String email, String password){

        _mAuth.createUserWithEmailAndPassword(email, password) //firebase auth method
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignUp", "createUserWithEmail:success");
                            FirebaseUser user = _mAuth.getCurrentUser();

                            //add User to firestore
                            //add user with document-id as generated user-id from firebase auth
                            CollectionReference dbUsers = _db.collection("users"); //reference collection from database
                            Map<String, Object> userObject = new HashMap<>(); //data type used to add to firestore
                            userObject.put("email", user.getEmail()); //only field needed

                            //add entry to collection of users in firestore
                            dbUsers.document(user.getUid()).set(userObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "User successfully added", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failure to add a user", Toast.LENGTH_SHORT).show();
                                }
                            });

                            //change to Home screen
                            startActivity(new Intent(getApplicationContext(), Home.class));

                        }else{
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //sign user with firebase
    private void signInUserWithEmailAndPassword(String email, String password){
        _mAuth.signInWithEmailAndPassword(email,password) //firebase auth method
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //change to Home screen
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }else{
                            // If sign in fails, display a message to the user.

                            Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
