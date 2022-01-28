package com.example.weatherme.Screens;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.weatherme.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class CityWeather extends AppCompatActivity {

    //data fields
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
        goBack = findViewById(R.id.goBack); //arrow to go back to previous screen


        //get selected cityData //retrieved from previous screen
        Bundle extras = getIntent().getExtras();
        if(extras != null){

            cityData = extras.getStringArrayList("cityModel"); //identify which screen

            //goBack //Logic performed to understand which screen came from
            goBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(cityData.get(3).equals("home")){ //only existent if coming from home screen
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
            requestQueue = Volley.newRequestQueue(this); //prepare queue
            parseJson(queryString); //call method to retrieve data

        } else{
            Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show(); //inform user
        }
    }

    //Retrieve API data
    private void parseJson(String url){

        if(url != null){

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() { //anonymous callback
                        @Override
                        public void onResponse(JSONObject response) { //receive JSON object from API
                            try {

                                //json format needs to be known
                                JSONObject jsonObjectWeather = response.getJSONObject("data").getJSONObject("current").getJSONObject("weather");
                                JSONObject jsonObjectPollution = response.getJSONObject("data").getJSONObject("current").getJSONObject("pollution");
                                //update UI with according data
                                cityTemperature.setText(String.format("Temperature: %s°C", jsonObjectWeather.getString("tp")));
                                cityWind.setText(String.format("Wind Speed: %s m/s", jsonObjectWeather.getString("ws")));
                                cityPollution.setText(String.format("Pollution: %s (AQI)", jsonObjectPollution.getString("aqius")));
                                cityPressure.setText(String.format("Atmospheric Pressure: %s hPA", jsonObjectWeather.getString("pr")));
                                cityHumidity.setText(String.format("Humidity: %s%%", jsonObjectWeather.getString("hu")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Could not load weather", Toast.LENGTH_SHORT).show(); //inform user
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not load weather", Toast.LENGTH_SHORT).show(); //inform user
                }
            });

            requestQueue.add(request); //perform request
        }
    }
}
