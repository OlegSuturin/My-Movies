package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.adapters.ReviewAdapter;
import com.example.mymovies.adapters.TrailerAdapter;
import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.data.Review;
import com.example.mymovies.data.Trailer;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private TextView textViewOverview;
    private ImageView imageViewStar;
    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private ScrollView scrollViewInfo;

    private MainViewModel viewModel;
    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;
    private static String lang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        lang = Locale.getDefault().getLanguage();  //получили язык установленный на устройстве

        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);
        imageViewStar = findViewById(R.id.imageViewAddToFavourite);
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        scrollViewInfo = findViewById(R.id.scrollViewInfo);

        Intent intent = getIntent();                                    //! проверяем Интент и наличие параметров
        if (intent != null && intent.hasExtra("id")) {
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

        // String urlVideos = NetworkUtils.buildURLVideos(id).toString();
        // Log.i("!@#", urlVideos);
        // String urlReviews = NetworkUtils.buildURLReviews(id).toString();
        // Log.i("!@#", urlReviews);


        JSONObject jsonObjectTrailer = NetworkUtils.getJSONFForVideos(movie.getId(), lang);
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailer);
        TrailerAdapter trailerAdapter = new TrailerAdapter(trailers);
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setAdapter(trailerAdapter);

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void trailerOnClick(String url) {
               //Toast.makeText(DetailActivity.this, url, Toast.LENGTH_SHORT).show();
                Intent  intentYoutube = new Intent(Intent.ACTION_VIEW, Uri.parse(url));                 //Вызываем неявный интент со ссылкой на youtube
                startActivity(intentYoutube);
            }
        });


        JSONObject jsonObjectReview = NetworkUtils.getJSONForReviews(movie.getId(), lang);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReview);
        ReviewAdapter reviewAdapter = new ReviewAdapter(reviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);

        scrollViewInfo.smoothScrollTo(0,0);   //устанавливаем скролл на начало экрана

    } //end of onCreate()

    //------------------------------------------------------------------------------------------------------

    //СОЗДАЕМ МЕНЮ - переопределяем метод onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //реакция на нажатие пунктов меню - переопределяем мето onOptionsItemSelected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();       //считываем id пункта меню
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    //-------------------------------------------------------------------------------------------------------

    public void setColorStar() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);   //получение фильма по ID в таблице избранного
        if (favouriteMovie == null) {                                 //устанавливаем звезду
            imageViewStar.setImageResource(R.drawable.graystar2);
        } else {
            imageViewStar.setImageResource(R.drawable.yellowstar);
        }
    }


    public void onClickChangeFavourite(View view) {     //  СЛУШАТЕЛЬ на нажатие на звезду - добавить/удалить в Избранное

        if (favouriteMovie == null) {                 //проверяем, что в избранном нет такого фильма
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));     //сохраняем movie в таблицу favourite_movie (ПРЕОБРАЗОВАНИЕ ТИПОВ ЧЕРЕЗ КОНСТРУКТОР) !ь
            Toast.makeText(this, R.string.add_to_favourites, Toast.LENGTH_SHORT).show();
        } else {   //Если фильм существует в таблице, то удаляем его из избранного
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, R.string.delete_from_favourites, Toast.LENGTH_SHORT).show();
        }
        setColorStar();
    }
}