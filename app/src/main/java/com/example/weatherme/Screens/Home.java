package com.example.weatherme.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherme.Adapters.CityAdapter;
import com.example.weatherme.Models.CityModel;
import com.example.weatherme.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private static String API_KEY = "47f309e7-d912-4d21-baf7-5e19628781f0";

    private FirebaseAuth mAuth;
    private Spinner countrySp;
    private Spinner stateSp;

    ArrayList<String> arrayList_country;
    ArrayAdapter<String> arrayAdapter_country;

    ArrayList<String> arrayList_cyprus, arrayList_czech, arrayList_sweeden;
    ArrayAdapter<String> arrayAdapter_states;

    private RecyclerView mRecyclerView;
    private CityAdapter cityAdapter;
    private ArrayList<CityModel> cityModels;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        countrySp = findViewById(R.id.sp_country);
        stateSp = findViewById(R.id.sp_state);

        arrayList_country = new ArrayList<>();
        arrayList_country.add("Cyprus");
        arrayList_country.add("Czech Republic");

        arrayAdapter_country = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_custom, arrayList_country);
        countrySp.setAdapter(arrayAdapter_country);

        //sublist
        arrayList_cyprus = new ArrayList<>();
        arrayList_cyprus.add("Ammochostos");
        arrayList_cyprus.add("Larnaka");
        arrayList_cyprus.add("Nicosia");

        arrayList_czech = new ArrayList<>();
        arrayList_czech.add("Central Bohemia");
        arrayList_czech.add("Praha");
        arrayList_czech.add("Kralovehradecky");


        countrySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    arrayAdapter_states = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_custom, arrayList_cyprus);
                }else if(i == 1){
                    arrayAdapter_states = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_custom, arrayList_czech);
                }
                stateSp.setAdapter(arrayAdapter_states);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        stateSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cityModels.clear();
                parseJson();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Initialize navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set Home
        bottomNavigationView.setSelectedItemId(R.id.home);

        //Item Selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
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


        //RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cityModels = new ArrayList<>();
        //Json
        requestQueue = Volley.newRequestQueue(this);

    }

    private void parseJson(){

        String url = "https://api.airvisual.com/v2/cities?state=" + stateSp.getSelectedItem().toString() + "&country="+countrySp.getSelectedItem().toString()+"&key="+API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");

                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject data = jsonArray.getJSONObject(i);
                                String cityName = data.getString("city");

                                cityModels.add(new CityModel(cityName, stateSp.getSelectedItem().toString(), countrySp.getSelectedItem().toString()));
                            }

                            cityAdapter = new CityAdapter(Home.this, cityModels);
                            mRecyclerView.setAdapter(cityAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);

    }


}
