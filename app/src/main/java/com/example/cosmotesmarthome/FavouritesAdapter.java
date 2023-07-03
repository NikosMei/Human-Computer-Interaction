package com.example.cosmotesmarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder> {

    private Context context;
    List<FavouriteStation> items;
    private OnClickListener onClickListener;

    public FavouritesAdapter(Context context, List<FavouriteStation> items,OnClickListener onClickListener) {
        this.context = context;
        this.items = items;
        this.onClickListener = onClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView stationNameText;
        TextView stationFreqText;


        public MyViewHolder(final View view) {
            super(view);
            stationNameText = itemView.findViewById(R.id.favouritesNameText);
            stationFreqText = itemView.findViewById(R.id.favourites_FreqText);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.onClick(v, getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_station, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.stationFreqText.setText(String.valueOf(items.get(position).getStationFrequency()));
        holder.stationNameText.setText(String.valueOf(items.get(position).getStationName()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(View v, int position);
    }
}

