package com.example.hikemate.Hike;

import static com.example.hikemate.Hike.HikeDetail.HIKE_KEY;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hikemate.Converter.TimeConverter;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;


import java.util.ArrayList;

public class HikeAdapter extends  RecyclerView.Adapter<HikeAdapter.ViewHolder>{
    private ArrayList<Hike> hikeArrayList = new ArrayList<>();
    private Context context;
    private HikeDatabase db;

    public HikeAdapter(ArrayList<Hike> hikeArrayList, Context context) {
        this.hikeArrayList = hikeArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hike_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db = HikeDatabase.getInstance(context);
        Hike item = hikeArrayList.get(position);

        holder.txtHikeName.setText(item.getHikeName());
        holder.txtValueLocation.setText(item.getLocation());
        holder.txtValueDOH.setText(TimeConverter.formattedDate(item.getDate()));

        holder.txtValueLength.setText(String.valueOf(item.getLength()));



        Glide.with(context)
                .asBitmap()
                .load(db.hikeImageDao().getHikeImageById(hikeArrayList.get(position).getId()).getData())
                .placeholder(R.drawable.baseline_restart_alt_24)
                .error(R.drawable.baseline_error_outline_24)
                .into(holder.imageHike);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HikeDetail.class);
                intent.putExtra(HIKE_KEY, hikeArrayList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return hikeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ShapeableImageView imageHike;
        private TextView txtHikeName, txtValueLocation, txtValueDOH, txtValueLength ;
        private MaterialCardView parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageHike = itemView.findViewById(R.id.imageHike);
            txtHikeName = itemView.findViewById(R.id.txtHikeName);
            txtValueLocation = itemView.findViewById(R.id.txtValueLocation);
            txtValueDOH = itemView.findViewById(R.id.txtValueDOH);
            txtValueLength = itemView.findViewById(R.id.txtValueLength);
            parent = itemView.findViewById(R.id.parent);
        }
    }

}
