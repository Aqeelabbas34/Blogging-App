package com.example.mvp.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.mvp.R;
import com.example.mvp.model.BlogModel;
import com.example.mvp.viewmodel.ViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddUpdatePostActivity extends AppCompatActivity {

    private EditText titleEdit, contentEdit;
    private ImageView imageViewUpload;
    private Button btnSave, btnUpdate;
    private ViewModel viewModel;
    private Uri selectedImageUri;
    private String savedImagePath = "";
    private int blogId = -1; // Default invalid ID
    private boolean isEditMode = false; // Flag for editing mode
    private Toolbar toolbar;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    savedImagePath = saveImageToInternalStorage(selectedImageUri); // ✅ Save new image path
                    Glide.with(this).load(new File(savedImagePath)).into(imageViewUpload);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        titleEdit = findViewById(R.id.editTitle);
        contentEdit = findViewById(R.id.editContent);
        imageViewUpload = findViewById(R.id.imageView);
        btnSave = findViewById(R.id.btnSave);
        btnUpdate = findViewById(R.id.btnUpdate);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);

        imageViewUpload.setOnClickListener(v -> pickImageFromGallery());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Check for intent extras to determine if editing an existing post
        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_MODE")) {
            isEditMode = intent.getBooleanExtra("EDIT_MODE", false);
            if (isEditMode) {
                blogId = intent.getIntExtra("BLOG_ID", -1);
                titleEdit.setText(intent.getStringExtra("TITLE"));
                contentEdit.setText(intent.getStringExtra("CONTENT"));
                savedImagePath = intent.getStringExtra("IMAGE_PATH");

                if (savedImagePath != null && !savedImagePath.isEmpty()) {
                    Glide.with(this).load(new File(savedImagePath)).into(imageViewUpload);
                }

                btnSave.setVisibility(Button.GONE);
                btnUpdate.setVisibility(Button.VISIBLE);
            }
        }

        btnSave.setOnClickListener(v -> saveNewPost());
        btnUpdate.setOnClickListener(v -> updateExistingPost());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) { // Back button
            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    //function to pick image from gallery
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        imagePickerLauncher.launch(intent);
    }

    //function to save image in internal storage
    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File imageFile = new File(getFilesDir(), "IMG_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void saveNewPost() {
        String title = titleEdit.getText().toString();
        String content = contentEdit.getText().toString();
        String dateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        BlogModel post = new BlogModel(0, title, content, savedImagePath, dateTime);
        viewModel.createBlog(post);
        finish();
    }

    private void updateExistingPost() {
        if (blogId == -1) return; // Prevent update if invalid ID

        String title = titleEdit.getText().toString();
        String content = contentEdit.getText().toString();

        BlogModel updatedPost = new BlogModel(blogId, title, content, savedImagePath,
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));

        viewModel.updateBlog(updatedPost);
        finish();
    }
}
