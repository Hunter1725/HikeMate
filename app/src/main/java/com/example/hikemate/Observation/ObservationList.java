package com.example.hikemate.Observation;

import static com.example.hikemate.Hike.HikeDetail.HIKE_ID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.Observation;
import com.example.hikemate.Database.Model.ObservationImage;
import com.example.hikemate.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ObservationList extends AppCompatActivity {

    private MaterialToolbar toolbarListObservation;
    private RecyclerView detailsRecView;
    private TextView txtEmpty;
    private ShapeableImageView imageEmpty;
    private ObservationAdapter observationAdapter;
    private ArrayList<Observation> observationArrayList;
    private HikeDatabase db;
    private Button btnCreateObservation;
    private Observation deletedObservation;
    private ObservationImage deletedObservationImage;
    private Hike incomingHike;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_observation_list);
        getSupportActionBar().hide();
        initView();
        Intent intent = getIntent();
        incomingHike = intent.getParcelableExtra(HIKE_ID);
        initRecyclerview();

        toolbarListObservation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initRecyclerview();
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.LEFT) {
                deletedObservation = observationArrayList.get(position);
                deletedObservationImage = db.observationImageDao().getObservationImageById(deletedObservation.getId());

                //Dialog builder
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ObservationList.this, R.style.ThemeOverlay_App_MaterialAlertDialog);
                builder.setTitle("Delete person");
                builder.setMessage("Are you sure you want to delete person " + deletedObservation.getName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform guest login action
                        db.observationDao().deleteObservation(deletedObservation); // Delete from the database first
                        observationArrayList.remove(position); // Then remove from the list
                        observationAdapter.notifyItemRemoved(position);
                        Snackbar.make(detailsRecView, "The person " + deletedObservation.getName() + " was removed!", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db.observationDao().insert(deletedObservation); // Insert back to the database
                                        db.observationImageDao().insertImage(deletedObservationImage);
                                        observationArrayList.add(position, deletedObservation); // Add back to the list
                                        observationAdapter.notifyItemInserted(position);
                                        initRecyclerview();
                                    }
                                }).setActionTextColor(Color.parseColor("#0083BE"))
                                .show();
                        initRecyclerview();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initRecyclerview();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();
                Drawable icon = ContextCompat.getDrawable(ObservationList.this, R.drawable.ic_delete);
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                // Draw the red background
                paint.setColor(Color.parseColor("#F84949"));
                c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);

                // Draw the icon on the background
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                int iconTop = itemView.getTop() + iconMargin;
                int iconBottom = itemView.getBottom() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                icon.draw(c);
            }
        }
    };

    private void initRecyclerview() {

        Glide.get(ObservationList.this).setMemoryCategory(MemoryCategory.HIGH);
        db = HikeDatabase.getInstance(ObservationList.this);

        observationArrayList = (ArrayList<Observation>) db.observationDao().getObservationsForHike(incomingHike.getId());

        if(observationArrayList.isEmpty()){
            txtEmpty.setVisibility(View.VISIBLE);
            imageEmpty.setVisibility(View.VISIBLE);
            detailsRecView.setVisibility(View.GONE);
        }else{
            txtEmpty.setVisibility(View.GONE);
            imageEmpty.setVisibility(View.GONE);
            detailsRecView.setVisibility(View.VISIBLE);

            detailsRecView.setLayoutManager(new LinearLayoutManager(ObservationList.this));
            observationAdapter = new ObservationAdapter(observationArrayList, ObservationList.this);
            detailsRecView.setAdapter(observationAdapter);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(detailsRecView);
        }
    }


    private void initView() {
        detailsRecView = findViewById(R.id.detailsRecView);
        txtEmpty = findViewById(R.id.txtEmpty);
        imageEmpty = findViewById(R.id.imageEmpty);
        toolbarListObservation = findViewById(R.id.toolbarListObservation);
        db = HikeDatabase.getInstance(ObservationList.this);
        detailsRecView = findViewById(R.id.detailsRecView);


        btnCreateObservation = findViewById(R.id.btnCreateObservation);
        btnCreateObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ObservationList.this, ObservationActivity.class));
            }
        });
    }
}