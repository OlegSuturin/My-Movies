package com.oliverst.mymovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oliverst.mymovies.R;
import com.oliverst.mymovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

//Адаптер для RecyclerView - Фильмы
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    List<Movie> movies;
    OnPosterClickListener onPosterClickListener;   //объект ИНТЕРФЕЙСНОГО ТИПА слушателя на нажатие
    OnReachEndListener onReachEndListener;         //объект ИНТЕРФЕЙСНОГО ТИПА слушателя на достижение конца списка

    public MovieAdapter() {
        movies = new ArrayList<>();   //создали массив
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();  //всегда оповещаем адаптер об изменении массива
    }

    //МЕТОД - когда будем листать список фильмов, будем добавлять (погружать) их в этот же массив, а не заменять старый массив новым
    public void addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();   //всегда оповещаем адаптер об изменении массива
    }

    public List<Movie> getMovies() {
        return movies;
    }

    //создаем СЛУШАТЕЛЯ на нажатие на картинку (элемент RecyclerView)
    //ИНТЕРФЕЙС который содержит один метод
   public   interface OnPosterClickListener {
        void onPosterClick(int position);
    }

    // + сеттер на слушателя
    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    //создаем СЛУШАТЕЛЯ на определение - достижения просмотра последнего элемента -  для подгрузки остальных данных из интернета и размещения их на RecycleView
   public interface OnReachEndListener {
        void onReachEnd();        //метод вызываем при достижении конца листа - размещаем его в методе onBindViewHolder
        //т.к. онс связывает Холдер с ПОЗИЦИЕЙ элемента
    }

    // + сеттер на слушателя
    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    @NonNull
    @Override                   //передаем макет XML холдеру при создании холдера
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (movies.size() >= 20 && position > movies.size() - 4 && onReachEndListener != null) {        //Определяем что до конца списка осталось 4 постера, что бы начать загрузку данных ЗАВРАНЕЕ
            onReachEndListener.onReachEnd();    // вызываем метод интерфейса
        }

        Movie movie = movies.get(position);   //получили объект из массива № position
        // ИСПОЛЬЗУЕМ БИБЛИОТЕКУ picasso для загрузки избражения из сети
        Picasso.get().load(movie.getPosterPath()).into(holder.imageViewSmallPoster);
    }

    @Override       //сообщаем количество элементов массива на данный момент
    public int getItemCount() {
        return movies.size();
    }

    //Метод очистки recyclerView
    public void clear(){
        this.movies.clear();
        notifyDataSetChanged();  //всегда оповещаем адаптер об изменении массива
    }


    class MovieViewHolder extends RecyclerView.ViewHolder {              //Холдер - держатель View
        private ImageView imageViewSmallPoster;

        public MovieViewHolder(@NonNull View itemView) {           //КОНСТРУКТОР холдера
            super(itemView);
            imageViewSmallPoster = itemView.findViewById(R.id.imageViewSmallPoster);

            itemView.setOnClickListener(new View.OnClickListener() {        //РАЗМЕЩЕНИЕ слушателя на нажатие - в конструкторе ViewHolder
                @Override
                public void onClick(View v) {
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onPosterClick(getAdapterPosition());   // вызываем метод интерфейса
                    }
                }
            });


        }
    }
}
