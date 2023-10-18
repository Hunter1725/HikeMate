package com.example.hikemate.Observation;

import static com.example.hikemate.Observation.ObservationDetail.OBSERVATION_KEY;

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
import com.example.hikemate.Database.Model.Observation;
import com.example.hikemate.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;


import java.util.ArrayList;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ViewHolder>{
    private ArrayList<Observation> observationArrayList = new ArrayList<>();
    private Context context;
    private HikeDatabase db;

    public ObservationAdapter(ArrayList<Observation> observationArrayList, Context context) {
        this.observationArrayList = observationArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ObservationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_observation_adapter, parent, false);
        return new ObservationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservationAdapter.ViewHolder holder, int position) {
        db = HikeDatabase.getInstance(context);
        Observation item = observationArrayList.get(position);

        holder.txtObservationName.setText(item.getName());
        holder.txtValueComment.setText(item.getAdditionalComment());
        holder.txtValueObservationTime.setText(TimeConverter.formattedDate(item.getTimeObservation()));




        Glide.with(context)
                .asBitmap()
                .load(db.observationImageDao().getObservationImageById(observationArrayList.get(position).getId()).getData())
                .placeholder(R.drawable.baseline_restart_alt_24)
                .error(R.drawable.baseline_error_outline_24)
                .into(holder.imageObservation);
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
        private ShapeableImageView imageObservation;
        private TextView txtObservationName, txtValueObservationTime, txtValueComment;
        private MaterialCardView parent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageObservation = itemView.findViewById(R.id.imageObservation);
            txtObservationName = itemView.findViewById(R.id.txtObservationName);
            txtValueObservationTime = itemView.findViewById(R.id.txtValueObservationTime);
            txtValueComment = itemView.findViewById(R.id.txtValueComment);
            parent = itemView.findViewById(R.id.parent);
        }
    }

}