package com.oliverst.mymovies.utils;

import com.oliverst.mymovies.data.Movie;
import com.oliverst.mymovies.data.Review;
import com.oliverst.mymovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//получаем данные из JSON
public abstract class JSONUtils {
    private static final String KEY_RESULTS = "results";

    //список ключей - для отзывов
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";

    //список ключей - для трейлеров
    private static final String KEY_KEY_OF_VIDEO = "key";
    private static final String KEY_NAME = "name";
    private static final String BASE_YOUTUBE_URL = "https://youtube.com/watch?v=";

    //список ключей - для фильмов
    private static final String KEY_ID = "id";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";                  //размеры картинок - из документации источника данных - как часть пути картинке
    public static final String BIG_POSTER_SIZE = "w780";
    public static final String BACKDROP_POSTER_SIZE = "w780";

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

                jsonObjectMovie.getString(KEY_BACKDROP_PATH); // ?????

                double voteAverage = jsonObjectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = jsonObjectMovie.getString(KEY_RELEASE_DATE);
                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
                result.add(movie); //перенесли данные в ArrayList
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Метод - сделав запрос в JSON, получаем массив с отзывами
    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();
        JSONArray jsonArray = null;

        if (jsonObject == null) {
            return result;   //пустой
        }

        try {
            jsonArray = jsonObject.getJSONArray(KEY_RESULTS);        //массив JSON объектов-отзывов
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectReview = jsonArray.getJSONObject(i);  //извлекаем объекты-отзывы и раскладываем данные

                String author = jsonObjectReview.getString(KEY_AUTHOR);
                String content = jsonObjectReview.getString(KEY_CONTENT);

                Review review = new Review(author, content);
                result.add(review);                         //перенесли данные в ArrayList
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Метод - сделав запрос в JSON, получаем массив с ТРЕЙЛЕРАМИ
    public static ArrayList<Trailer> getTrailersFromJSON(JSONObject jsonObject) {
        ArrayList<Trailer> result = new ArrayList<>();
        JSONArray jsonArray = null;

        if (jsonObject == null) {
            return result;   //пустой
        }

        try {
            jsonArray = jsonObject.getJSONArray(KEY_RESULTS);        //массив JSON объектов-трейлеров
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectTrailers = jsonArray.getJSONObject(i);  //извлекаем объекты-трейлер и раскладываем данные

                String key = BASE_YOUTUBE_URL + jsonObjectTrailers.getString(KEY_KEY_OF_VIDEO);
                String name = jsonObjectTrailers.getString(KEY_NAME);

                Trailer trailer = new Trailer(key, name);
                result.add(trailer);                            //перенесли данные в ArrayList
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}




