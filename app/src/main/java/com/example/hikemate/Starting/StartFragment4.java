package com.example.hikemate.Starting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.hikemate.R;

public class StartFragment4 extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.getting_started_fragment, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);

        imageView.setImageResource(R.drawable.gettingstart4);
        descriptionTextView.setText(R.string.start_fragment4);

        return view;
    }
}
