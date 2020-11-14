package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPosters;
    private Switch switchSort;
    private TextView textViewPopilarity;
    private TextView textViewTopRated;

    private MovieAdapter movieAdapter;
    private JSONObject jsonObject;
    private ArrayList<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchSort = findViewById(R.id.switchSort);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        textViewPopilarity = findViewById(R.id.textViewPopilarity);
        textViewTopRated = findViewById(R.id.textViewTopRated);

        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));   //установили отображение сеткой 2
        recyclerViewPosters.setAdapter(movieAdapter);           //передали адаптер в RevyclerView
        switchSort.setChecked(true);  // установили в сортировку по рейтингу - Слушатель не срабатывает т.к. он определен ниже.

        //создали слушателя на свича
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);

            movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {     //СОЗДАЛИ Слушатель - срабатывает на нажатие на Постер - элемент RecycleView
                @Override
                public void onPosterClick(int position) {
                    Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                }
            });

          movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {            //СОЗДАЛИ Слушатель - срабатывает на достижение конца списка - для подгрузки данных
              @Override
              public void onReachEnd() {
                  Toast.makeText(MainActivity.this, "Конец списка", Toast.LENGTH_SHORT).show();

              }
          });




    } // end of onCreate()

    public void onClickTextViewPopilarity(View view) {
        switchSort.setChecked(false);
    }

    public void onClicktextViewTopRated(View view) {
        switchSort.setChecked(true);
    }

    //Метод - установка типа сортировки и получение соответствующих данных
    private void setMethodOfSort(boolean isToprated) {
        int methodOfSort;
        if (isToprated) {
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.teal_200));
            textViewPopilarity.setTextColor(getResources().getColor(R.color.white));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewPopilarity.setTextColor(getResources().getColor(R.color.teal_200));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white));
        }


        jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1);
        movies = JSONUtils.getMoviesFromJSON(jsonObject);
        movieAdapter.setMovies(movies);
    }


}