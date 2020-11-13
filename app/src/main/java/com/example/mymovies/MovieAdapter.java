package com.example.mymovies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

//Адаптер для RecyclerView
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    ArrayList<Movie> movies;

    public MovieAdapter() {
        movies = new ArrayList<>();   //создали массив
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();  //всегда оповещаем адаптер об изменении массива
    }

        //МЕТОД - когда будем листать список фильмов, будем добавлять (погружать) их в этот же массив, а не заменять старый массив новым
    public void adddMovies(ArrayList<Movie> movies){
        this.movies.addAll(movies);
        notifyDataSetChanged();   //всегда оповещаем адаптер об изменении массива
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }



    @NonNull
    @Override                   //передаем макет XML холдеру при создании холдера
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);   //получили объект из массива № position

                                // ИСПОЛЬЗУЕМ БИБЛИОТЕКУ picasso для загрузки избражения из сети
        Picasso.get().load(movie.getPosterPath()).into(holder.imageViewSmallPoster);
    }

    @Override       //сообщаем количество элементов массива на данный момент
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{              //Холдер - держатель View
            private ImageView imageViewSmallPoster;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);


        }
    }
}
