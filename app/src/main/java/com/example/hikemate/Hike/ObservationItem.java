package com.example.hikemate.Hike;

import static com.example.hikemate.Observation.ObservationDetail.OBSERVATION_KEY;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hikemate.Converter.TimeConverter;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Observation;
import com.example.hikemate.Observation.ObservationDetail;
import com.example.hikemate.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

public class ObservationItem extends RecyclerView.Adapter<ObservationItem.ViewHolder>{
    private ArrayList<Observation> observationArrayList = new ArrayList<>();
    private Context context;
    private HikeDatabase db;

    public ObservationItem(ArrayList<Observation> observationArrayList, Context context) {
        this.observationArrayList = observationArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.observation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationItem.ViewHolder holder, int position) {
        db = HikeDatabase.getInstance(context);
        Observation item = observationArrayList.get(position);

        holder.txtName.setText(item.getName());
        holder.txtDate.setText(TimeConverter.formattedDate(item.getTimeObservation()));


        Glide.with(context)
                .asBitmap()
                .load(db.observationImageDao().getObservationImageById(observationArrayList.get(position).getId()).getData())
                .placeholder(R.drawable.baseline_restart_alt_24)
                .error(R.drawable.baseline_error_outline_24)
                .into(holder.image);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ObservationDetail.class);
                intent.putExtra(OBSERVATION_KEY, observationArrayList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return observationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ShapeableImageView image;
        private TextView txtName, txtDate;
        private MaterialCardView parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            txtName = itemView.findViewById(R.id.txtName);
            txtDate = itemView.findViewById(R.id.txtDate);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
