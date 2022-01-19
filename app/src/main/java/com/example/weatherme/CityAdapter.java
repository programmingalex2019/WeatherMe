package com.example.weatherme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
    }

    @Override
    public int getItemCount() {
        return mCityModels.size();
    }

    public class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewCityName;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewCityName = itemView.findViewById(R.id.cityName);
        }
    }

}
