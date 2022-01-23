package com.example.weatherme.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.weatherme.Adapters.CityAdapter;
import com.example.weatherme.Adapters.FavoriteCityAdapter;
import com.example.weatherme.Data.MyDatabase;
import com.example.weatherme.Data.myDAO;
import com.example.weatherme.Models.CityModel;
import com.example.weatherme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Favorites extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FavoriteCityAdapter favoriteCitiesAdapter;
    private ArrayList<CityModel> cityModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mRecyclerView = findViewById(R.id.favorites_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        //Initialize navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set Home
        bottomNavigationView.setSelectedItemId(R.id.favorites);

        //Item Selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.observations:
                        startActivity(new Intent(getApplicationContext(), Observations.class));
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
        Executors.newSingleThreadExecutor().execute(() -> {

            final myDAO myDAO = MyDatabase.getDatabase(this).myDAO();
            cityModels = new ArrayList<CityModel>(myDAO.getAllModules());
            favoriteCitiesAdapter = new FavoriteCityAdapter(Favorites.this, cityModels);
            mRecyclerView.setAdapter(favoriteCitiesAdapter);
            runOnUiThread(() -> favoriteCitiesAdapter.notifyDataSetChanged());

            favoriteCitiesAdapter.setOnItemClickListener(new FavoriteCityAdapter.OnItemClickListener() {
                @Override
                public void onDeleteClick(int position) {

                    Executors.newSingleThreadExecutor().execute(() -> {

                        final myDAO myDAO = MyDatabase.getDatabase(getApplicationContext()).myDAO();
                        myDAO.delete(cityModels.get(position));
                        cityModels.remove(position);
                        runOnUiThread(() -> favoriteCitiesAdapter.notifyItemRemoved(position));

                    });
                }
            });

        });


    }
}
