package com.example.weatherme.Screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherme.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LocationListener {

    //fields
    private static String API_KEY = "47f309e7-d912-4d21-baf7-5e19628781f0";
    
    private TextView cityCountry;
    private TextView cityTemperature;
    private Button signIn;
    private Button signGuest;
    private LocationManager locationManager;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize fields
        cityCountry = findViewById(R.id.cityCountry);
        cityTemperature = findViewById(R.id.cityDegrees);
        //initialize queue for requests
        mQueue = Volley.newRequestQueue(this);

        signIn = findViewById(R.id.signIn);
        signGuest = findViewById(R.id.guestSign);

        //runtime permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},100);
        }

        //access latitude and longitude -> request for weather -> update UI
        getLocationAndWeather();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), SignIn.class);
                startActivity(myIntent);
            }
        });

        signGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Home.class);
                startActivity(myIntent);
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getLocationAndWeather(){

        try{

            //access android resources
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //store coordinates
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            //request details string
            String urlString = "https://api.airvisual.com/v2/nearest_city?lat="+ latitude +"&lon="+ longitude +"&key="+ API_KEY;

            //JSON GET REQUEST -> using Volley library
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null,
                    new Response.Listener<JSONObject>(){
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(JSONObject response){
                            try{

                                Log.d("LOCATION", response.getJSONObject("data").toString());
                                JSONObject jsonObject = response.getJSONObject("data"); //main object
                                //access individual objects and json fields
                                cityCountry.setText(String.format("%s,%s",jsonObject.getString("city"),  jsonObject.getString("country")));
                                cityTemperature.setText(String.format("%s°C",jsonObject.getJSONObject("current").getJSONObject("weather").getString("tp")));

                            }catch (JSONException e){
                                cityCountry.setText("Could not load data");
                                e.printStackTrace(); //prints all errors
                            }
                        }

                    }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error){
                    error.printStackTrace(); //prints all errors
                }
            });

            mQueue.add(request); //add request to queue


        }catch(Exception e){
            e.printStackTrace(); //prints all errors
        }

    }

    // must have due to interface implementation //not used
    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}
