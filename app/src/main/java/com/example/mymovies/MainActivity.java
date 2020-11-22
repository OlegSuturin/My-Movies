package com.example.mymovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovies.adapters.MovieAdapter;
import com.example.mymovies.data.MainViewModel;
import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {
    private RecyclerView recyclerViewPosters;
    private Switch switchSort;
    private TextView textViewPopilarity;
    private TextView textViewTopRated;
    private ProgressBar progressBarLoading;

    // private MovieAdapter movieAdapter;
    private MovieAdapter movieAdapter;
    private JSONObject jsonObject;
    //private ArrayList<Movie> movies;

    private MainViewModel viewModel;

    private static final int LOADER_ID = 133; // - уникальный идентификатор загрузчика, определяем сами
    private LoaderManager loaderManager;      //  - менеджер загрузок

    private static int page = 1;    // переменная, которая хранит номер страницы загружаемых фильмов, увеличивается каждый раз на 1;
    private static boolean isLoading = false;     // используется при подгрузке данных, чтобы метод подгрузки не вызывался несколько раз, пока данные грузятся
    private static int methodOfSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchSort = findViewById(R.id.switchSort);
        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        textViewPopilarity = findViewById(R.id.textViewPopilarity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        loaderManager = LoaderManager.getInstance(this);       // получение доступа к loaderManager  используется паттерн SINGLETON

        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, 2));   //установили отображение сеткой 2
        recyclerViewPosters.setAdapter(movieAdapter);           //передали адаптер в RevyclerView
        switchSort.setChecked(true);  // установили в сортировку по рейтингу - Слушатель не срабатывает т.к. он определен ниже.

        //создали слушателя на свича
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;                   //установили на начальное значение 1-я страница
                setMethodOfSort(isChecked);         //вызывает загрузку данных
            }
        });
        switchSort.setChecked(false);

        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {     //СОЗДАЛИ Слушатель - срабатывает на нажатие на Постер - элемент RecycleView
            @Override
            public void onPosterClick(int position) {
                // Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
                Movie movie = movieAdapter.getMovies().get(position);

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());

                //movies.get(position).getId()
                startActivity(intent);
            }
        });

        movieAdapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {            //СОЗДАЛИ Слушатель - срабатывает на достижение конца списка - для подгрузки данных
            @Override
            public void onReachEnd() {
                if (!isLoading) {   //если процесс загрузки не идет
                    Toast.makeText(MainActivity.this, "Конец списка", Toast.LENGTH_SHORT).show();    // код ПОДГРУЗКИ ДАННЫХ
                    downLoadData(methodOfSort, page);
                }
            }
        });

        //из Интернета заполняем свой ArrayList movies, из movies заполняем БД - downLoadData() (Live data -> Recyclerview = отображение - onChanged() observer)
        LiveData<List<Movie>> moviesFromLivedata = viewModel.getMovies();    //получили объект LiveData
        moviesFromLivedata.observe(this, new Observer<List<Movie>>() {    //назначаем его Observer  - просматриваемый
            @Override
            public void onChanged(List<Movie> moviesFromLivedata) {          //метод запускается каждый раз, как изменяется список (синхронизирован с записями БД);
                //   movieAdapter.setMovies(moviesFromLivedata);             //Метод отвечает за  - обновление данные на RecyclerView  - ОТОБРАЖЕНИЕ
                // movieAdapter.notifyDataSetChanged();
                if(page == 1){
                    movieAdapter.setMovies(moviesFromLivedata);    // если отсутствует интернет устанавливаем на адаптер фильмы из БД
                                                                    //ЛОГИКА ПРИЛОЖЕНИЯ
                }                                                   //если только запустили приложение или изменили метод сортировки т.е. page=1 - начинаем подгрузку данных
                                                                    //если интернета нет - то подгрузка данных не произойдет - данные беруться из БД (moviesFromLivedata)) - можно пользоваться приложением без связи
                                                                    //если интернет есть - то загрузятся новые денные в загрузчике, все данные БД очистятся (см.onLoadFinished) и сохранятся новые значения

            }
        });

    } // end of onCreate()
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
    //Методы onСlick на Textview
    public void onClickTextViewPopilarity(View view) {
        switchSort.setChecked(false);
    }

    public void onClicktextViewTopRated(View view) {
        switchSort.setChecked(true);
    }

    //Метод - установка типа сортировки и получение соответствующих данных
    private void setMethodOfSort(boolean isToprated) {

        if (isToprated) {
            methodOfSort = NetworkUtils.TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.teal_200));
            textViewPopilarity.setTextColor(getResources().getColor(R.color.white));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            textViewPopilarity.setTextColor(getResources().getColor(R.color.teal_200));
            textViewTopRated.setTextColor(getResources().getColor(R.color.white));
        }
        downLoadData(methodOfSort, page);     //загружаем данные из интернета и заполняем БД
    }

    //вынесли загрузку данных в отдельный метод
    //из Интернета заполняем свой ArrayList movies, из movies заполняем БД (Live data -> Recyclerview = отображение)
    private void downLoadData(int methodOfSort, int page) {
        URL url = NetworkUtils.buildURL(methodOfSort, page);
        //  Log.i("!@#", url.toString());
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);     //запускаем ЗАГРУЗЧИК

     /*   jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, page);
        movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (movies != null && !movies.isEmpty()){        //проверили, что данные получили
            viewModel.deleteAllMovie();             //очищаем все предидущие данные в БД
                for(Movie movie: movies){              // заполняб БД
                    viewModel.insertMovie(movie);
                }
        }*/
    }

    //--------------------------------------------три метода----------от implements LoaderManager.LoaderCallbacks<JSONObject>
    @NonNull
    @Override
    // в данном метода создаем загрузчик
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {                    // id - уникальный идентификатор загрузчика, определяем сами
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);    //args - объект бандл
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {           //СЛУШАТЕЛЬ на начало загрузки данных
            @Override
            public void onStartLoading() {
                progressBarLoading.setVisibility(View.VISIBLE);
                isLoading = true;         //загрузка началась
            }
        });
        return jsonLoader;
    }

    @Override
    //в этом методе получаем данные по окончании работы загрузчика
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (movies != null && !movies.isEmpty()) {        //проверили, что данные получили
            if (page == 1) {
                viewModel.deleteAllMovie();             //очищаем все предидущие данные в БД если начинаем смотреть с 1-й страницы
                movieAdapter.clear();
            }
            for (Movie movie : movies) {              // заполняб БД по одному фильму, при этом каждый раз вызывается метод onChanged() обсервера
                viewModel.insertMovie(movie);
            }
            movieAdapter.addMovies(movies);                     //добавление фильмов в адаптер перенесли из обсервера сюда, чтобы сделать это сразу один раз на 20 фильмов
            page++;    //загрузили страницу из 20-ти фильмов, увеличили на 1;
        }

        isLoading = false;                //загрузка закончилась
        progressBarLoading.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(LOADER_ID);    //после загрузки данных удаляем загрузчик
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {
        //оставляем пустым
    }
} // end of class