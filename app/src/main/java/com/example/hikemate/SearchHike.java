package com.example.hikemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Hike.HikeAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SearchHike extends AppCompatActivity {

    private MaterialToolbar toolbarSearching;
    private SearchView searchView;
    private NestedScrollView nestedScrollView;
    private TextView txtEmpty;
    private RecyclerView resultRecView;
    private FloatingActionButton fabScrollToTop;
    private HikeDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hike);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getSupportActionBar().hide();
        initView();

        searchView.requestFocus();

        initListener();


    }

    private void initListener() {
        toolbarSearching.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()) {
                    String name = "%" + newText + "%";
                    ArrayList<Hike> hikeArrayList = (ArrayList<Hike>) db.hikeDao().searchHike(name);
                    if (!hikeArrayList.isEmpty()) {
                        txtEmpty.setVisibility(View.GONE);
                        resultRecView.setVisibility(View.VISIBLE);
                        HikeAdapter hikeAdapter = new HikeAdapter(hikeArrayList, SearchHike.this);
                        resultRecView.setAdapter(hikeAdapter);
                        resultRecView.setLayoutManager(new LinearLayoutManager(SearchHike.this));
                    } else {
                        txtEmpty.setVisibility(View.VISIBLE);
                        resultRecView.setVisibility(View.GONE);
                    }
                } else {
                    resultRecView.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    toolbarSearching.setNavigationIcon(null);
                    searchView.setIconifiedByDefault(false);
                } else {
                    toolbarSearching.setNavigationIcon(R.drawable.ic_back);
                    searchView.setIconifiedByDefault(true);
                }
            }
        });

        fabScrollToTop.hide();
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Check the scroll position
                if (scrollY == 0) {
                    // Scroll is at the top, hide the FloatingActionButton
                    fabScrollToTop.hide();
                } else if (scrollY > oldScrollY) {
                    // Scrolling downwards, hide the FloatingActionButton
                    fabScrollToTop.hide();
                } else {
                    // Scrolling upwards, show the FloatingActionButton
                    fabScrollToTop.show();
                }
            }
        });

        //FAB scroll up
        // Set an OnClickListener for the FloatingActionButton
        fabScrollToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to the top of the NestedScrollView
                nestedScrollView.smoothScrollTo(0, 0);
            }
        });
    }

    private void initView() {
        toolbarSearching = findViewById(R.id.toolbarSearching);
        searchView = findViewById(R.id.searchView);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        txtEmpty = findViewById(R.id.txtEmpty);
        resultRecView = findViewById(R.id.resultRecView);
        fabScrollToTop = findViewById(R.id.fabScrollToTop);
        db = HikeDatabase.getInstance(SearchHike.this);
    }
}