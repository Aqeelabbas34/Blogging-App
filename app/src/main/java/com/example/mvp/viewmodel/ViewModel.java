package com.example.mvp.viewmodel;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.mvp.model.BlogModel;
import com.example.mvp.repository.Repository;
import java.util.List;

public class ViewModel extends AndroidViewModel {
    private Repository repository;

    public ViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }
    public void createBlog(BlogModel post) {
        repository.createBlog(post);
    }
    public List<BlogModel> fetchBlogs() {
        return repository.fetchBlogs();
    }



    public void updateBlog(BlogModel post) {
        repository.updateBlog(post);
    }

    public void deleteBlog(BlogModel post) {
        repository.deleteBlog(post.getId());
    }

    public void deleteMultiple(List<Integer> ids) {
        repository.deleteBlogs(ids);
    }
}
