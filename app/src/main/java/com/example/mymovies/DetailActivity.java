package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ImageView imageViewStar;

    private MainViewModel viewModel;
    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        imageViewStar = findViewById(R.id.imageViewAddToFavourite);


        Intent intent = getIntent();                                    //! проверяем Интент и наличие параметров
        if (intent != null && intent.hasExtra("id")){
            id = intent.getIntExtra("id", -1);
        } else {
            finish();               //  закрываем активность, если что то не так
        }

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        movie = viewModel.getMovieById(id);

        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());
        setColorStar();

    } //end of onCreate()

    public void setColorStar(){
        favouriteMovie = viewModel.getFavouriteMovieById(id);   //получение фильма по ID в таблице избранного
        if(favouriteMovie == null){                                 //устанавливаем звезду
            imageViewStar.setImageResource(R.drawable.graystar2);
        }else{
            imageViewStar.setImageResource(R.drawable.yellowstar);
        }
    }


    public void onClickChangeFavourite(View view) {     // добавить/удалить в Избранное

            if(favouriteMovie == null){                 //проверяем, что в избранном нет такого фильма
                viewModel.insertFavouriteMovie(new FavouriteMovie(movie));     //сохраняем movie в таблицу favourite_movie (ПРЕОБРАЗОВАНИЕ ТИПОВ ЧЕРЕЗ КОНСТРУКТОР) !ь
                Toast.makeText(this,R.string.add_to_favourites, Toast.LENGTH_SHORT).show();
            }else {   //Если фильм существует в таблице, то удаляем его из избранного
                viewModel.deleteFavouriteMovie(favouriteMovie);
                Toast.makeText(this, R.string.delete_from_favourites, Toast.LENGTH_SHORT).show();
            }
            setColorStar();
    }
}