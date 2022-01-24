package com.example.weatherme.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherme.Adapters.CityAdapter;
import com.example.weatherme.Models.CityModel;
import com.example.weatherme.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class CityWeather extends AppCompatActivity {

    //data variables
    private ArrayList<String> cityData;

    //content variables
    private TextView cityCountry;
    private TextView cityTemperature;
    private TextView cityWind;
    private TextView cityPollution;
    private TextView cityPressure;
    private TextView cityHumidity;
    private ImageView goBack;

    //API variables
    private static String API_KEY = "47f309e7-d912-4d21-baf7-5e19628781f0";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_weather);

        //initialize Views
        cityCountry = findViewById(R.id.cityCountry_weather);
        cityTemperature = findViewById(R.id.temperature);
        cityWind = findViewById(R.id.wind);
        cityPollution = findViewById(R.id.pollution);
        cityPressure = findViewById(R.id.pressure);
        cityHumidity = findViewById(R.id.humidity);
        goBack = findViewById(R.id.goBack);


        //get selected cityData
        Bundle extras = getIntent().getExtras();
        if(extras != null){

            cityData = extras.getStringArrayList("cityModel");

            //goBack
            goBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(cityData.get(3).equals("home")){
                        startActivity(new Intent(CityWeather.this, Home.class));
                    }else{
                        startActivity(new Intent(CityWeather.this, Favorites.class));
                    }
                }
            });

            //set UI
            cityCountry.setText(String.format("%s, %s", cityData.get(0), cityData.get(1)));

            //Get JsonData
            String queryString = "https://api.airvisual.com/v2/city?city="+cityData.get(0)+"&state="+cityData.get(1)+"&country="+cityData.get(2)+"&key="+API_KEY;
            requestQueue = Volley.newRequestQueue(this);
            parseJson(queryString);

        } else{
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseJson(String url){

        if(url != null){

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Log.i("Url", url);

                                JSONObject jsonObjectWeather = response.getJSONObject("data").getJSONObject("current").getJSONObject("weather");
                                JSONObject jsonObjectPollution = response.getJSONObject("data").getJSONObject("current").getJSONObject("pollution");

                                cityTemperature.setText(String.format("Temperature: %s°C", jsonObjectWeather.getString("tp")));
                                cityWind.setText(String.format("Wind Speed: %s m/s", jsonObjectWeather.getString("ws")));
                                cityPollution.setText(String.format("Pollution: %s (AQI)", jsonObjectPollution.getString("aqius")));
                                cityPressure.setText(String.format("Atmospheric Pressure: %s hPA", jsonObjectWeather.getString("pr")));
                                cityHumidity.setText(String.format("Humidity: %s%%", jsonObjectWeather.getString("hu")));

                                Log.i("json", jsonObjectWeather.toString());

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


}
