package com.example.weatherme.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherme.Data.MyDatabase;
import com.example.weatherme.Data.myDAO;
import com.example.weatherme.Models.CityModel;
import com.example.weatherme.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private Context mContext;
    private ArrayList<CityModel> mCityModels;

    public CityAdapter(Context context, ArrayList<CityModel> cityModels){
        mContext = context;
        mCityModels = cityModels;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.city_card, parent, false);
        return new CityViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {

        CityModel currentCity = mCityModels.get(position);

        String cityName = currentCity.getCityName();

        holder.mTextViewCityName.setText(cityName);

        holder.mAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mAddToFavorite.isChecked()){

                    Executors.newSingleThreadExecutor().execute(() -> {
                        final myDAO myDAO = MyDatabase.getDatabase(mContext).myDAO();
                        myDAO.insert(mCityModels.get(position));

                        List<CityModel> models = myDAO.getAllModules();
                        for(CityModel i : models){
                            Log.i("City", i.getCityName() + " " + i.getCityCountry());
                        }

                    });



                    Toast.makeText(mContext, "Item: " + position + " added", Toast.LENGTH_SHORT).show();
                }else{

                    Executors.newSingleThreadExecutor().execute(() -> {
                        final myDAO myDAO = MyDatabase.getDatabase(mContext).myDAO();
                        myDAO.delete(mCityModels.get(position));

                        List<CityModel> models = myDAO.getAllModules();
                        for(CityModel i : models){
                            Log.i("City", i.getCityName() + " " + i.getCityCountry());
                        }

                    });

                    Toast.makeText(mContext, "Item: " + position + " removed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCityModels.size();
    }

    public class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewCityName;
        public CheckBox mAddToFavorite;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewCityName = itemView.findViewById(R.id.cityName);
            mAddToFavorite = itemView.findViewById(R.id.add_favorite);
        }
    }

}
