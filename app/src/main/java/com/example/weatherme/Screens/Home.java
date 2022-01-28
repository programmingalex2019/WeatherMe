package com.example.weatherme.Screens;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private static String API_KEY = "47f309e7-d912-4d21-baf7-5e19628781f0"; //weather API key

    private FirebaseAuth _mAuth; //Firebase

    //main recycler view of cities
    private RecyclerView mRecyclerView;
    private CityAdapter cityAdapter; //API between data and UI
    private ArrayList<CityModel> cityModels; //models
    private RequestQueue requestQueue; //json requests

    //sub spinners acting as filters -> countries and states
    private Spinner countrySp;
    private Spinner stateSp;
    ArrayList<String> arrayList_country;
    ArrayAdapter<String> arrayAdapter_country;
    ArrayList<String> arrayList_cyprus, arrayList_czech;
    ArrayAdapter<String> arrayAdapter_states;

    //check if user connected to network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //get from android
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo(); //get network context
        return activeNetworkInfo != null && activeNetworkInfo.isConnected(); //check if connected
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        //initialize spinners
        countrySp = findViewById(R.id.sp_country);
        stateSp = findViewById(R.id.sp_state);

        //static sample data for sub lists (COUNTRIES)
        arrayList_country = new ArrayList<>();
        arrayList_country.add("Cyprus");
        arrayList_country.add("Czech Republic");

        //bind data to adapter
        arrayAdapter_country = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_custom, arrayList_country);
        countrySp.setAdapter(arrayAdapter_country);

        //sublist with static data (STATES)
        //CYPRUS
        arrayList_cyprus = new ArrayList<>();
        arrayList_cyprus.add("Ammochostos");
        arrayList_cyprus.add("Larnaka");
        arrayList_cyprus.add("Nicosia");
        //CZECH REPUBLIC
        arrayList_czech = new ArrayList<>();
        arrayList_czech.add("Central Bohemia");
        arrayList_czech.add("Praha");
        arrayList_czech.add("Kralovehradecky");

        //Set sub list spinner listeners - when clicked -> countries
        countrySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //selection logic -> 0 -> Cyprus : 1 -> Czech -> initialize according state adapter to country
                if(i == 0){
                    arrayAdapter_states = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_custom, arrayList_cyprus);
                }else if(i == 1){
                    arrayAdapter_states = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_custom, arrayList_czech);
                }
                stateSp.setAdapter(arrayAdapter_states); //assign adapter
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Set sub list spinner listeners - when clicked -> states
        stateSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cityModels.clear(); //empty list of city models before fetching
                parseJson(); //GET API DATA
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Initialize navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set Home
        bottomNavigationView.setSelectedItemId(R.id.home);

        //Item Selected listener -> dependent on bottom navigation icon selected -> switch according screen
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.favorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.observations:
                        //Feature allowed only for sign in users
                        if(FirebaseAuth.getInstance().getCurrentUser() != null) //perform user signed in check
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


        //Main RecyclerView initialization
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cityModels = new ArrayList<>();
        //queue instance -> to be used to add a request
        requestQueue = Volley.newRequestQueue(this);

    }

    private void parseJson(){

        if(!isNetworkAvailable()){
            Toast.makeText(getApplicationContext(), "Please connect to internet", Toast.LENGTH_LONG).show(); //inform user no internet
        }else{

            //data is coming from selected country and state (spinners)
            String url = "https://api.airvisual.com/v2/cities?state=" + stateSp.getSelectedItem().toString() + "&country="+countrySp.getSelectedItem().toString()+"&key="+API_KEY;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() { //anonymous callback
                        @Override
                        public void onResponse(JSONObject response) { //receive JSON object from API
                            try {

                                //access json by key names -> need to be known
                                JSONArray jsonArray = response.getJSONArray("data"); //array of jsonObjects

                                //for each city in state add to city models
                                for(int i = 0; i < jsonArray.length(); i++){

                                    JSONObject data = jsonArray.getJSONObject(i);
                                    String cityName = data.getString("city");

                                    cityModels.add(new CityModel(cityName, stateSp.getSelectedItem().toString(), countrySp.getSelectedItem().toString()));
                                }

                                //after data fetched and modeled -> assign adapter to main city recyclerView
                                cityAdapter = new CityAdapter(Home.this, cityModels);
                                mRecyclerView.setAdapter(cityAdapter);

                                //assign a listener for each item in the recycler view - when clicked.
                                cityAdapter.setOnItemClickListener(new CityAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        //Transfer cityModel data to CityWeather screen for API weather request
                                        Intent i = new Intent(Home.this, CityWeather.class);
                                        ArrayList<String> cityData = new ArrayList<>();
                                        cityData.add(cityModels.get(position).getCityName());
                                        cityData.add(cityModels.get(position).getCityState());
                                        cityData.add(cityModels.get(position).getCityCountry());
                                        cityData.add("home"); //added to know which screen came from -> for goBack button in CityWeather
                                        i.putExtra("cityModel", cityData); //attached to intent
                                        startActivity(i); //change screen
                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Could not load cities", Toast.LENGTH_SHORT).show(); //inform user
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not load cities", Toast.LENGTH_SHORT).show(); //inform user
                }
            });
            requestQueue.add(request); //proceed with the request
        }
    }


}
