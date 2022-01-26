package com.example.weatherme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherme.Models.ObservationModel;
import com.example.weatherme.R;

import java.util.ArrayList;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder> {

    private Context mContext;
    private ArrayList<ObservationModel> mObservationModels;
    private OnItemClickListener mListener;

    public ObservationAdapter(Context context, ArrayList<ObservationModel> observationModels) {

        this.mContext = context;
        this.mObservationModels = observationModels;
    }

    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public class ObservationViewHolder extends RecyclerView.ViewHolder{

        public TextView mObservationTitle;
        public ImageView mRemoveObservation;

        public ObservationViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            mObservationTitle = itemView.findViewById(R.id.observationTitle);
            mRemoveObservation = itemView.findViewById(R.id.removeObservation);
            
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

            mRemoveObservation.setOnClickListener(new View.OnClickListener() {
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


    @NonNull
    @Override
    public ObservationAdapter.ObservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.observation_card, parent, false);
        return new ObservationViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationAdapter.ObservationViewHolder holder, int position) {

        ObservationModel observationModel = mObservationModels.get(position);

        String observationTitle = observationModel.getTitle();

        holder.mObservationTitle.setText(observationTitle);

    }

    @Override
    public int getItemCount() {
        return mObservationModels.size();
    }
}
