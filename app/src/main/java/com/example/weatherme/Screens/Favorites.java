package com.example.weatherme.Screens;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.weatherme.Adapters.FavoriteCityAdapter;
import com.example.weatherme.Data.MyDatabase;
import com.example.weatherme.Data.myDAO;
import com.example.weatherme.Models.CityModel;
import com.example.weatherme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class Favorites extends AppCompatActivity {

    //Main recycler view of favorite cities -> from room
    private RecyclerView mRecyclerView;
    private FavoriteCityAdapter favoriteCitiesAdapter;
    private ArrayList<CityModel> cityModels;

    //check if user connected to network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //initialize fields and prepare recycle view
        mRecyclerView = findViewById(R.id.favorites_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Initialize navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set Home
        bottomNavigationView.setSelectedItemId(R.id.favorites);

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
                    case R.id.account:
                        startActivity(new Intent(getApplicationContext(), Account.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }


    //happens when activity resumed
    @Override
    protected void onResume() {
        super.onResume();

        //execute on separate thread to avoid main thread long process
        Executors.newSingleThreadExecutor().execute(() -> {

            final myDAO myDAO = MyDatabase.getDatabase(this).myDAO(); //interface between database and app
            cityModels = new ArrayList<CityModel>(myDAO.getAllModules()); //retrieve cityModels from database
            favoriteCitiesAdapter = new FavoriteCityAdapter(Favorites.this, cityModels); //initialize adapter
            mRecyclerView.setAdapter(favoriteCitiesAdapter); //set adapter to recycler view
            runOnUiThread(() -> favoriteCitiesAdapter.notifyDataSetChanged()); //apply changes to UI -> hence on UI thread

            //custom click listener using an interface
            favoriteCitiesAdapter.setOnItemClickListener(new FavoriteCityAdapter.OnItemClickListener() {
                @Override
                public void onDeleteClick(int position) { //intention to delete favoriteCity from recycler view / database

                    //execute on separate thread to avoid main thread long process
                    Executors.newSingleThreadExecutor().execute(() -> {

                        final myDAO myDAO = MyDatabase.getDatabase(getApplicationContext()).myDAO();
                        myDAO.delete(cityModels.get(position)); //position retrieved from adapter
                        cityModels.remove(position); //remove from arrayList
                        runOnUiThread(() -> favoriteCitiesAdapter.notifyItemRemoved(position)); //notify adapter and apply UI changes

                    });
                }

                @Override
                public void onItemClick(int position) { //intention to view city weather data -> move to new Screen
                    if(!isNetworkAvailable()){ //internet must be on
                        Toast.makeText(getApplicationContext(), "Please connect to internet", Toast.LENGTH_LONG).show();
                    }else{
                        //prepare new Intent and pass data to CityWeather Screen to fetch API data
                        Intent i = new Intent(Favorites.this, CityWeather.class);
                        ArrayList<String> cityData = new ArrayList<>();
                        cityData.add(cityModels.get(position).getCityName());
                        cityData.add(cityModels.get(position).getCityState());
                        cityData.add(cityModels.get(position).getCityCountry());
                        cityData.add("favorites"); //indicate activity from
                        i.putExtra("cityModel", cityData); //extras to be retrieved on intended screen
                        startActivity(i);
                    }
                }
            });
        });
    }
}
