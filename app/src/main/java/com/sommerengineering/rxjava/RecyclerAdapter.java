package com.sommerengineering.rxjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.SimpleViewHolder> {

    private List<Post> posts = new ArrayList<>();

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post_list_item, null, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<Post> posts){
        this.posts = posts;
        notifyDataSetChanged();
    }

    public void updatePost(Post post){
        posts.set(posts.indexOf(post), post);
        notifyItemChanged(posts.indexOf(post));
    }

    public List<Post> getPosts(){
        return posts;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder{

        TextView title, numComments;
        ProgressBar progressBar;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            numComments = itemView.findViewById(R.id.num_comments);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }

        public void bind(Post post){
            title.setText(post.getTitle());

            if(post.getComments() == null){
                showProgressBar(true);
                numComments.setText("");
            }
            else{
                showProgressBar(false);
                numComments.setText(String.valueOf(post.getComments().size()));
            }
        }

        private void showProgressBar(boolean showProgressBar){
            if(showProgressBar) {
                progressBar.setVisibility(View.VISIBLE);
            }
            else{
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
