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
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;}

    public class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextViewCityName;
        public CheckBox mAddToFavorite;

        public CityViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTextViewCityName = itemView.findViewById(R.id.cityName);
            mAddToFavorite = itemView.findViewById(R.id.remove_favorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }


    public CityAdapter(Context context, ArrayList<CityModel> cityModels){
        mContext = context;
        mCityModels = cityModels;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.city_card, parent, false);
        return new CityViewHolder(v, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {

        //initialize card item
        CityModel currentCity = mCityModels.get(position);
        String cityName = currentCity.getCityName();
        holder.mTextViewCityName.setText(cityName);

        //check if item already in database -> set UI to favorite
        Executors.newSingleThreadExecutor().execute(() -> {

            //initialize database
            final myDAO myDAO = MyDatabase.getDatabase(mContext).myDAO();

            //check if city in database -> if set icon to favorite
            List<CityModel> models = myDAO.getAllModules();

            for(CityModel i: models){
                if(i.getCityName().equals(mCityModels.get(position).getCityName())){
                    holder.mAddToFavorite.setChecked(true);
                    Log.i("EXISTS", mCityModels.get(position).getCityName() + " Exists");
                }
            }
        });

        holder.mAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mAddToFavorite.isChecked()){

                    Executors.newSingleThreadExecutor().execute(() -> {

                        //initialize database
                        final myDAO myDAO = MyDatabase.getDatabase(mContext).myDAO();
                        myDAO.insert(mCityModels.get(position));

                    });

                    Toast.makeText(mContext, mCityModels.get(position).getCityName() + " was added to favorites", Toast.LENGTH_SHORT).show();

                }else{

                    Executors.newSingleThreadExecutor().execute(() -> {
                        final myDAO myDAO = MyDatabase.getDatabase(mContext).myDAO();
                        myDAO.delete(mCityModels.get(position));

                        List<CityModel> models = myDAO.getAllModules();

                    });

                    Toast.makeText(mContext, mCityModels.get(position).getCityName() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCityModels.size();
    }



}
