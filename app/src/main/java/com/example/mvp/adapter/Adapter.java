package com.example.mvp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mvp.R;
import com.example.mvp.model.BlogModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.BlogViewHolder> {

    private Context context;
    private List<BlogModel> blogList;
    private List<BlogModel> originalList;
    private OnItemClickListener listener;
    private OnSelectionChangeListener selectionChangeListener;
    private boolean isMultiSelectMode = false;
    private List<BlogModel> selectedPosts = new ArrayList<>();

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(BlogModel post);
    }

    // Interface for selection change listener to notify the main activity about changes
    public interface OnSelectionChangeListener {
        void onSelectionChanged(int count);
    }

    // Adapter constructor
    public Adapter(Context context, List<BlogModel> blogList, OnItemClickListener listener, OnSelectionChangeListener selectionListener) {
        this.context = context;
        this.blogList = blogList;
        this.originalList = new ArrayList<>(blogList);
        this.listener = listener;
        this.selectionChangeListener = selectionListener;
    }

    @NotNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        // Inflate  item layout and return the ViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.item_blog_post, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull BlogViewHolder holder, int position) {
        BlogModel post = blogList.get(position);

        // Set data for each view in the item i.e title,image,content
        holder.postTitle.setText(post.getTitle());
        holder.postDate.setText(post.getDateTime());
        holder.description.setText(post.getContent());
        Glide.with(context).load(post.getImagePath()).into(holder.postImage);

        // Show checkbox only in multi-select mode
        holder.checkBox.setVisibility(isMultiSelectMode ? View.VISIBLE : View.GONE);

        // Set the checkbox state based on whether the post is selected
        holder.checkBox.setChecked(selectedPosts.contains(post));

        // Handle click to select/deselect in multi-select mode or open post in single-select mode
        holder.itemView.setOnClickListener(v -> {
            if (isMultiSelectMode) {
                toggleSelection(post, holder); // Toggle selection when in multi-select mode
            } else {
                listener.onItemClick(post); // Single-click to open the post
            }
        });

        // Long press to enable multi-select mode
        holder.itemView.setOnLongClickListener(v -> {
            if (!isMultiSelectMode) {
                multiSelectMode(true); // Enable multi-select mode if not already enabled
            }
            toggleSelection(post, holder); // Toggle selection on long press
            return true; // Consume the event
        });

        // Handle checkbox change for selecting or deselecting a post
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !selectedPosts.contains(post)) {
                selectedPosts.add(post); // Add post to selected list if checked
            } else if (!isChecked && selectedPosts.contains(post)) {
                selectedPosts.remove(post); // Remove post from selected list if unchecked
            }

            // Notify the main activity about the number of selected items
            if (selectionChangeListener != null) {
                selectionChangeListener.onSelectionChanged(selectedPosts.size());
            }

            // Exit multi-select mode if no items are selected
            if (selectedPosts.isEmpty()) {
                multiSelectMode(false); // Disable multi-select mode if nothing is selected
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogList.size(); // Return the size of the blog list
    }

    // Toggle the selection state of an item (select/deselect)
    private void toggleSelection(BlogModel post, BlogViewHolder holder) {
        if (selectedPosts.contains(post)) {
            selectedPosts.remove(post); // If post is already selected, remove it
            holder.checkBox.setChecked(false); // Uncheck the checkbox
        } else {
            selectedPosts.add(post); // If post is not selected, add it to the selected list
            holder.checkBox.setChecked(true); // Check the checkbox
        }

        // Notify the main activity about the number of selected items
        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(selectedPosts.size());
        }

        // Exit multi-select mode if no items are selected
        if (selectedPosts.isEmpty()) {
            multiSelectMode(false); // Disable multi-select mode if nothing is selected
        }
    }

    // Filter the blog list based on the search text
    public void filter(String text) {
        blogList.clear(); // Clear the current blog list
        if (text.isEmpty()) {
            blogList.addAll(originalList); // If no search text, restore the original list
        } else {
            text = text.toLowerCase(); // Convert the search text to lowercase
            for (BlogModel post : originalList) {
                if (post.getTitle().toLowerCase().contains(text)) {
                    blogList.add(post); // Add matching posts to the blog list
                }
            }
        }

        // Instead of directly calling notifyDataSetChanged(), we use post() to updateBlog the UI after the layout calculation phase
        new android.os.Handler().post(() -> notifyDataSetChanged()); // Ensures that the notifyDataSetChanged() happens after layout calculations
    }

    // Update the adapter data with a new list of blog posts
    public void updateData(List<BlogModel> newPosts) {
        originalList.clear(); // Clear the original list
        originalList.addAll(newPosts); // Add all new posts to the original list
        blogList.clear(); // Clear the current blog list
        blogList.addAll(newPosts); // Add the new posts to the blog list

        // Post the updateBlog to the RecyclerView to avoid issues during layout calculation
        new android.os.Handler().post(() -> notifyDataSetChanged()); // Delays notifyDataSetChanged() to avoid conflicts with layout
    }

    // Enable or disable multi-select mode
    public void multiSelectMode(boolean multiSelect) {
        if (isMultiSelectMode != multiSelect) {
            isMultiSelectMode = multiSelect; // Set the multi-select mode flag
            if (!multiSelect) {
                selectedPosts.clear(); // Clear the selected posts when exiting multi-select mode
            }

            // Delay notifyDataSetChanged() to ensure it happens after layout
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> notifyDataSetChanged()); // Ensures that the notifyDataSetChanged() happens after layout calculations
        }
    }

    // Get the list of selected posts
    public List<BlogModel> getSelectedPosts() {
        return selectedPosts;
    }

    // uncheck all boxes
    public void clearSelection() {
        selectedPosts.clear(); // Clear the selected posts list
        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(0); // Notify that no items are selected
        }

        // Post/delay the updateBlog to RecyclerView to avoid issues during layout calculation
        new android.os.Handler().post(() -> notifyDataSetChanged()); // Ensures that the notifyDataSetChanged() happens after layout calculations
    }

    // ViewHolder class to hold the views for each item
    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postDate, description;
        ImageView postImage;
        CheckBox checkBox;

        public BlogViewHolder(@NotNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle); // Find the title TextView
            postDate= itemView.findViewById(R.id.postDate); // Find the date TextView
            description = itemView.findViewById(R.id.description); // Find the description TextView
            postImage   = itemView.findViewById(R.id.postImage); // Find the image ImageView
            checkBox = itemView.findViewById(R.id.checkBox); // Find the CheckBox for multi-select
        }
    }
}
