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
import com.example.weatherme.Screens.Favorites;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class FavoriteCityAdapter extends RecyclerView.Adapter<FavoriteCityAdapter.FavoriteCityViewHolder> {

    private Context mContext;
    private ArrayList<CityModel> mCityModels;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public class FavoriteCityViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewCityName;
        public CheckBox mAddToFavorite;

        public FavoriteCityViewHolder(@NonNull View itemView, final OnItemClickListener listener) {

            super(itemView);
            mTextViewCityName = itemView.findViewById(R.id.cityName);
            mAddToFavorite = itemView.findViewById(R.id.add_favorite);

            mAddToFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }

    }

    public FavoriteCityAdapter(Context context, ArrayList<CityModel> cityModels){ this.mContext = context; this.mCityModels = cityModels;}

    @NonNull
    @Override
    public FavoriteCityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.city_card, parent, false);
        return new FavoriteCityViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCityViewHolder holder, int position) {

        CityModel currentCity = mCityModels.get(position);

        String cityName = currentCity.getCityName();

        holder.mTextViewCityName.setText(cityName);

        holder.mAddToFavorite.setChecked(true);


    }

    @Override
    public int getItemCount() {
        return mCityModels.size();
    }


}
