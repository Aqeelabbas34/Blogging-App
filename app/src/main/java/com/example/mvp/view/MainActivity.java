package com.example.mvp.view;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mvp.R;
import com.example.mvp.adapter.Adapter;
import com.example.mvp.model.BlogModel;
import com.example.mvp.viewmodel.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewModel viewModel;
    private Adapter adapter;
    private RecyclerView recyclerView;
    private View btnDelete;
    private TextView tvBlogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvBlogs = findViewById(R.id.tvBlogs);
        tvBlogs.setVisibility(View.VISIBLE);
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setVisibility(View.GONE); // Hide deleteBlog button initially

        adapter = new Adapter(this, new ArrayList<>(), post -> {
            Intent intent = new Intent(MainActivity.this, ViewBlogPostActivity.class);
            intent.putExtra("BLOG_ID", post.getId());
            startActivity(intent);
        }, selectedCount -> {
            // Show deleteBlog button only when items are selected
            tvBlogs.setVisibility(selectedCount > 0 ? View.GONE : View.VISIBLE);
            btnDelete.setVisibility(selectedCount > 0 ? View.VISIBLE : View.GONE);
        });

        recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // Search functionality
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });

        // Floating Action Button to Add New Post
        findViewById(R.id.fabAdd).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, AddUpdatePostActivity.class))
        );

        // Delete selected posts
        btnDelete.setOnClickListener(v -> {
            List<BlogModel> selected = adapter.getSelectedPosts();
            if (!selected.isEmpty()) {
                List<Integer> ids = new ArrayList<>();
                for (BlogModel post : selected) {
                    ids.add(post.getId());
                }
                viewModel.deleteMultiple(ids);
                adapter.multiSelectMode(false);
                adapter.clearSelection();
                adapter.updateData(viewModel.fetchBlogs());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.updateData(viewModel.fetchBlogs());
    }
}
