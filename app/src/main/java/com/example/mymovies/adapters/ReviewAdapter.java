package com.example.mymovies.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.data.Review;

import java.util.ArrayList;
import java.util.List;

//Адаптер на RecyclerView - Отзывы
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewsViewHolder> {
    private ArrayList<Review> reviews;

    public ReviewAdapter(ArrayList<Review> reviews) {   //конструктор адаптера
        this.reviews = reviews;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();      //обновление адаптера при изменении данных
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);

        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.textViewAuthor.setText(review.getAuthor());
        holder.textViewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewAuthor;
        private TextView textViewContent;

        public ReviewsViewHolder(@NonNull View itemView) {    //конструктор ViewHolder
            super(itemView);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewContent = itemView.findViewById(R.id.textViewContent);
        }
    }


}
