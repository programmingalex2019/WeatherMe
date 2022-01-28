package com.example.weatherme.Screens;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.weatherme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Account extends AppCompatActivity {

    //UI fields
    private TextView signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account); //assign layout

        signOut = findViewById(R.id.signOut); //assign UI

        //when clicked user must logOut from firebase
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut(); //sign out user
                startActivity(new Intent(getApplicationContext(), Landing.class)); //return to Landing page
            }
        });

        //Initialize navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set Home
        bottomNavigationView.setSelectedItemId(R.id.account);

        //Item Selected listener -> dependent on bottom navigation icon selected -> switch according screen
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.observations:
                        if(FirebaseAuth.getInstance().getCurrentUser() != null)
                        {
                            startActivity(new Intent(getApplicationContext(), Observations.class));
                            overridePendingTransition(0,0);
                            return true;
                        }else{
                            Toast.makeText(getApplicationContext(), "Only signed in users can use this feature", Toast.LENGTH_LONG).show();
                        }
                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}
