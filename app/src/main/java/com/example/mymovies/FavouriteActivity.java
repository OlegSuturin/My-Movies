package com.example.mymovies;
//Активность показывает список всех Избранных фильмов
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mymovies.data.FavouriteMovie;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {
    private RecyclerView recyclerviewFavouriteMovies;      //вывод аналогичен, как на RecyclerView главной активности, поэтому используем тот же адаптер
    private MovieAdapter adapter;
    private MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        recyclerviewFavouriteMovies = findViewById(R.id.recyclerViewFavouriteMovies);
        recyclerviewFavouriteMovies.setLayoutManager(new GridLayoutManager(this, 2));   //установили разметку Recyclerview
        adapter = new MovieAdapter();
        recyclerviewFavouriteMovies.setAdapter(adapter);

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);   //обязательно получаем доступ к viewModel
        LiveData<List<FavouriteMovie>> moviesFavoriteFromLivedata = viewModel.getFavouriteMoviesLiveData();                // LiveData связанная с БД
        moviesFavoriteFromLivedata.observe(this, new Observer<List<FavouriteMovie>>() {                         //создаем Обсервер
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
               List<Movie> movies = new ArrayList<>();
                if(favouriteMovies != null){                // обязательно проверяем список на null

                    movies.addAll(favouriteMovies);                         // организуем вывод данных
                    adapter.setMovies(movies);
                    adapter.notifyDataSetChanged();
                }else{
                    // finish();
                }
            }
        });



            adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {     //СОЗДАЛИ Слушатель - срабатывает на нажатие на Постер - элемент RecycleView
        @Override
        public void onPosterClick(int position) {
            // Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            Movie movie = adapter.getMovies().get(position);

            Intent intent = new Intent(FavouriteActivity.this, DetailActivity.class);
            intent.putExtra("id", movie.getId());

            //movies.get(position).getId()
            startActivity(intent);
        }
    });
    } // end of onCreate();


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
        switch (id){
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


}