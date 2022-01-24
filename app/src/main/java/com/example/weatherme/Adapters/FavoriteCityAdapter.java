package com.example.weatherme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherme.Models.CityModel;
import com.example.weatherme.R;

import java.util.ArrayList;

public class FavoriteCityAdapter extends RecyclerView.Adapter<FavoriteCityAdapter.FavoriteCityViewHolder> {

    private Context mContext;
    private ArrayList<CityModel> mCityModels;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public class FavoriteCityViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewCityName;
        public ImageView mAddRemoveFavorite;

        public FavoriteCityViewHolder(@NonNull View itemView, final OnItemClickListener listener) {

            super(itemView);
            mTextViewCityName = itemView.findViewById(R.id.cityName);
            mAddRemoveFavorite = itemView.findViewById(R.id.remove_favorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            mAddRemoveFavorite.setOnClickListener(new View.OnClickListener() {
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
        View v = LayoutInflater.from(mContext).inflate(R.layout.city_favorite_card, parent, false);
        return new FavoriteCityViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCityViewHolder holder, int position) {

        CityModel currentCity = mCityModels.get(position);

        String cityName = currentCity.getCityName();

        holder.mTextViewCityName.setText(cityName);

    }

    @Override
    public int getItemCount() {
        return mCityModels.size();
    }


}
