package com.example.mymovies.utils;

import com.example.mymovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//получаем данные из JSON
public abstract class JSONUtils {
    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";                  //размеры картинок - из документации источника данных - как часть пути картинке
    public static final String BIG_POSTER_SIZE = "w780";
    public static final String BACKDROP_POSTER_SIZE = "w780";

    //список ключей
    private static final String KEY_RESULTS = "results";

    private static final String KEY_ID = "id";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PASH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELESE_DATE = "release_date";


    // Метод - сделав запрос в JSON, получаем массив с фильмами
    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        ArrayList<Movie> result = new ArrayList<>();
        JSONArray jsonArray = null;

        if (jsonObject == null) {
            return result;   //пустой
        }

        try {
            jsonArray = jsonObject.getJSONArray(KEY_RESULTS);        //массив JSON объектов-фильмов
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectMovie = jsonArray.getJSONObject(i);  //извлекаем объекты-фильмы и раскладываем данные
                int id = jsonObjectMovie.getInt(KEY_ID);
                int voteCount = jsonObjectMovie.getInt(KEY_VOTE_COUNT);
                String title = jsonObjectMovie.getString(KEY_TITLE);
                String originalTitle = jsonObjectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = jsonObjectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + jsonObjectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + jsonObjectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = BASE_POSTER_URL + BACKDROP_POSTER_SIZE + jsonObjectMovie.getString(KEY_POSTER_PATH);
                jsonObjectMovie.getString(KEY_BACKDROP_PASH);
                double voteAverage = jsonObjectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = jsonObjectMovie.getString(KEY_RELESE_DATE);
                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
                result.add(movie); //перенесли данные в ArrayList
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

}




