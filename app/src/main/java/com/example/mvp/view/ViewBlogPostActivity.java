package com.example.mvp.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mvp.R;
import com.example.mvp.model.BlogModel;
import com.example.mvp.viewmodel.ViewModel;

import java.io.File;

public class ViewBlogPostActivity extends AppCompatActivity {

    private ViewModel viewModel;
    private BlogModel blogModel;
    private TextView viewPostTitle, viewPostContent;
    private ImageView viewImage;
    private Button btnShare;
    private Toolbar toolbar;
    private int blogId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_blog_post);

        viewPostTitle = findViewById(R.id.viewTitle);
        viewPostContent = findViewById(R.id.viewContent);
        viewImage = findViewById(R.id.viewImage);
        btnShare = findViewById(R.id.btnShare);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Blog Details");
        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setTint(getResources().getColor(R.color.white));
            toolbar.setOverflowIcon(overflowIcon);
        }


        blogId = getIntent().getIntExtra("BLOG_ID", -1);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        // fetch blog post from DB
        for (BlogModel post : viewModel.fetchBlogs()) {
            if (post.getId() == blogId) {
                blogModel = post;
                showData();
                break;
            }
        }

        btnShare.setOnClickListener(v -> sharePost());
    }

    private void showData() {
        viewPostTitle.setText(blogModel.getTitle());
        viewPostContent.setText(blogModel.getContent());

        // Load image from internal storage file path
        Glide.with(this)
                .load(new File(blogModel.getImagePath()))
                .into(viewImage);
    }

    private void sharePost() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        // Share text content
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, blogModel.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, blogModel.getContent());

        File imageFile = new File(blogModel.getImagePath());
        if (imageFile.exists()) {
            //  Get URI using FileProvider
            Uri imageUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    imageFile
            );

            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            // If image doesn't exist, share text only
            shareIntent.setType("text/plain");
        }

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) { // Back button
            finish();
            return true;
        } else if (id == R.id.action_edit) {
            updatePost();
            return true;
        } else if (id == R.id.action_delete) {
            confirmDelete();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updatePost() {
        Intent intent = new Intent(ViewBlogPostActivity.this, AddUpdatePostActivity.class);
        intent.putExtra("BLOG_ID", blogModel.getId());
        intent.putExtra("TITLE", blogModel.getTitle());
        intent.putExtra("CONTENT", blogModel.getContent());
        intent.putExtra("IMAGE_PATH", blogModel.getImagePath());
        intent.putExtra("EDIT_MODE", true);  // Flag to indicate editing mode
        startActivity(intent);

    }


    // ✅ Confirm Delete with AlertDialog
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to deleteBlog this post?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteBlog(blogModel);
                    finish(); // Close activity after deleteBlog
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    //updating activity to show data on resume
    @Override
    protected void onResume() {
        super.onResume();
        for (BlogModel post : viewModel.fetchBlogs()) {
            if (post.getId() == blogId) {
                blogModel = post;
                showData();
                break;
            }
        }
    }
}
