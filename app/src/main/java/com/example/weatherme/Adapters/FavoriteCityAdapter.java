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

    //Fields
    private Context mContext;
    private ArrayList<CityModel> mCityModels;
    private OnItemClickListener mListener;

    //overwrite click listener -> so can be accessed from other screens/activities
    public interface OnItemClickListener{
        void onDeleteClick(int position); //delete a city from favorites
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    //individual card
    public class FavoriteCityViewHolder extends RecyclerView.ViewHolder {

        //properties
        public TextView mTextViewCityName;
        public ImageView mAddRemoveFavorite;

        //constructor
        public FavoriteCityViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            //initialize fields
            mTextViewCityName = itemView.findViewById(R.id.cityName);
            mAddRemoveFavorite = itemView.findViewById(R.id.remove_favorite);

            //when item clicked //functionality will be added when click listener implemented
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

            //remove -> when clicked on checked star
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

    //constructor
    public FavoriteCityAdapter(Context context, ArrayList<CityModel> cityModels){ this.mContext = context; this.mCityModels = cityModels;}

    //inflate each with a card
    @NonNull
    @Override
    public FavoriteCityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.city_favorite_card, parent, false);
        return new FavoriteCityViewHolder(v, mListener);
    }

    //on bind
    @Override
    public void onBindViewHolder(@NonNull FavoriteCityViewHolder holder, int position) {

        //initialize fields //data from holder
        CityModel currentCity = mCityModels.get(position);

        String cityName = currentCity.getCityName();

        holder.mTextViewCityName.setText(cityName);

    }

    //important -> size to be know so position can be retrieved
    @Override
    public int getItemCount() {
        return mCityModels.size();
    }


}
