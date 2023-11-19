package com.example.hikemate.HikeKnowledge;

import static com.example.hikemate.HikeKnowledge.AnimalDetail.ANIMAL_KEY;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hikemate.Database.Model.Animal;
import com.example.hikemate.Other.GetCurrentLanguage;
import com.example.hikemate.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder> {
    private List<Animal> itemList;
    private Context context;

    public AnimalAdapter(List<Animal> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    public AnimalAdapter(Context context) {
        this.context = context;
    }
    //    private OnItemClickListener itemClickListener;
//
//    public interface OnItemClickListener {
//        void onItemClick(int position);
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        itemClickListener = listener;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animals, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Animal item = itemList.get(position);

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.baseline_downloading_24)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.itemImage);
        if(GetCurrentLanguage.getCurrentLanguage(context).equals("vi")){
            holder.itemName.setText(item.getNameVi());
            holder.itemDanger.setText(item.getDanger());
        }else {
            holder.itemName.setText(item.getNameEn());
            holder.itemDanger.setText(item.getDanger());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AnimalDetail.class);
                intent.putExtra(ANIMAL_KEY, item);
                context.startActivity(intent);
            }
        });
    }

    public void setItemList(List<Animal> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView itemImage;
        public TextView itemName;
        public TextView itemDanger;
        public MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemDanger = itemView.findViewById(R.id.itemDanger);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}

