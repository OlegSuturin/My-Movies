package com.example.mymovies.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

// класс ViewModel - работа с БД в отдельном прогграммном потоке

public class MainViewModel extends AndroidViewModel {
    private static MovieDatabase database;
    private LiveData<List<Movie>> moviesLiveData;    //объект LiveData для хранения всех записей

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MovieDatabase.getInstance(getApplication());   // создаем/получаем БД
        moviesLiveData = database.movieDao().getAllMovies();     //считываем все записи из таблицы
    }

    public LiveData<List<Movie>> getMovies() {    //Геттер на все записи
        return moviesLiveData;
    }

    // МЕТОДЫ работы с БД - в отдельных потоках , для каждого создан класс Task
    public void insertMovie(Movie movie) {
        new InsertTask().execute(movie);
    }

    private static class InsertTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().isertMovie(movies[0]);
            }
            return null;
        }
    }

    public void deleteMovie(Movie movie) {
        new DeleteTask().execute(movie);
    }

    private static class DeleteTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.movieDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    public void deleteAllMovie() {
        new DeleteAllTask().execute();
    }

    private static class DeleteAllTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
                database.movieDao().deleteAllMovies();
            return null;
        }
    }

    public Movie getMovieById(int id) {
        try {
            return new GetMovieByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetMovieByIdTask extends AsyncTask<Integer, Void, Movie> {       //первый параметр - принимает, последний - возвращает
        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
               return database.movieDao().getMovieById(integers[0]);   // возвращаем результат Movie
            }
            return null;
        }
    }


} // end of class