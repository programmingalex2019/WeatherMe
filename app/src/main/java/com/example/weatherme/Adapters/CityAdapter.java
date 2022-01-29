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

    //fields
    private Context mContext;
    private ArrayList<CityModel> mCityModels;
    private OnItemClickListener mListener;

    //overwrite click listener to be called in other screens
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){mListener = listener;}

    //one Card view in the recycler view class
    public class CityViewHolder extends RecyclerView.ViewHolder {

        //fields/content
        public TextView mTextViewCityName;
        public CheckBox mAddToFavorite;

        //constructor
        public CityViewHolder(@NonNull View itemView, final OnItemClickListener listener) { //pass listener -> so that can be inflated
            super(itemView);
            //initialize fields
            mTextViewCityName = itemView.findViewById(R.id.cityName);
            mAddToFavorite = itemView.findViewById(R.id.remove_favorite);

            //implement click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        //position comes from adapter
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position); //specific item at position will be clicked
                        }
                    }
                }
            });

        }
    }

    //Adapter constructor
    public CityAdapter(Context context, ArrayList<CityModel> cityModels){
        mContext = context;
        mCityModels = cityModels;
    }

    //binding
    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.city_card, parent, false); //inflate recycler view with custom layout
        return new CityViewHolder(v, mListener);
    }

    //when binded
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

            //compare with database of favorites and identify if star needs to be true
            for(CityModel i: models){
                if(i.getCityName().equals(mCityModels.get(position).getCityName())){
                    holder.mAddToFavorite.setChecked(true); //favorited
                }
            }
        });

        //when user clicks the star checkbox
        holder.mAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mAddToFavorite.isChecked()){ //when clicked

                    //initialize database on new thread
                    Executors.newSingleThreadExecutor().execute(() -> {

                        //initialize database
                        final myDAO myDAO = MyDatabase.getDatabase(mContext).myDAO();
                        myDAO.insert(mCityModels.get(position)); //add to database

                    });

                    Toast.makeText(mContext, mCityModels.get(position).getCityName() + " was added to favorites", Toast.LENGTH_SHORT).show();

                }else{

                    //uncheck -> remove from favorites database
                    Executors.newSingleThreadExecutor().execute(() -> {
                        final myDAO myDAO = MyDatabase.getDatabase(mContext).myDAO();
                        myDAO.delete(mCityModels.get(position));
                    });

                    Toast.makeText(mContext, mCityModels.get(position).getCityName() + " was removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //important -> size to be know so position can be retrieved
    @Override
    public int getItemCount() {
        return mCityModels.size();
    }

}
