package com.example.mymovies.utils;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

//вся работа с сетью
//API key 3bcb031a3c3fcdb49409692e8fb88be9
//Пример запроса - https://api.themoviedb.org/3/discover/movie?api_key=3bcb031a3c3fcdb49409692e8fb88be9&language=en-US&sort_by=vote_average.desc&include_adult=false&include_video=false&page=2
//Пример пути к картинке https://image.tmdb.org/t/p/w500/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg
public class NetworkUtils {
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";


    private static final String PARAMS_API_KEY = "api_key";   //параметры
    private static final String PERAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";

    private static final String API_KEY = "3bcb031a3c3fcdb49409692e8fb88be9";     //их значения
    private static final String LANGUAGE_VALUE = "ru-ru";
    private static final String SORT_BY_POPULARITY = "popularity.desc";  // сорт.по популярности убыв.
    private static final String SORT_BY_ROP_RATED = "vote_average.desc"; //по средней оценке убыв.

    public static final int POPULARITY = 0; //для метода- который принимает int, в зависимости от вида сортировки выдает разные результаты
    public static final int TOP_RATED = 1;


    //МЕТОД, ФОРМИРУЕС СТРОКУ ЗАПРОСА URL
    private static URL buildURL(int sortBy, int page) {
        URL resultURL = null;   // присв null, т.к. преобразование может выбросить исключение
        String methodSortBy;

        if (sortBy == POPULARITY) {             //проверяем - какой вид сортировки выбран
            methodSortBy = SORT_BY_POPULARITY;
        } else {
            methodSortBy = SORT_BY_ROP_RATED;
        }
        //формируем строку запроса ? = & вст. автоматически
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)  //добпвляем параметры
                .appendQueryParameter(PERAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY, methodSortBy)     //установили метод сортировки
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();

        try {
            resultURL = new URL(uri.toString());    //может выбросить исключение
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return resultURL;
    }


    //МЕТОД - ЗАГРУЗКА ДАННЫХ ИЗ ИНТЕРНЕТА - исполняется в др.программном потоке
    public static JSONObject getJSONFromNetwork(int sortBy, int page) {
        JSONObject jsonObject = null;
        URL url = buildURL(sortBy, page); //формируем url
        //Log.i("!@#", url.toString());
        try {
            JSONLoadTask task = new JSONLoadTask();
            jsonObject = task.execute(url).get();   // запускаем загрузку в другом программном потоке
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return jsonObject;
        // результат - готовый JSON
    }

    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject jsonObject = null;

            if (urls == null || urls.length == 0) {            //обязательно проверять URL
                return jsonObject; // будет = null
            } else {
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) urls[0].openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        stringBuilder.append(line);
                        line = bufferedReader.readLine();
                    }

                    jsonObject = new JSONObject(stringBuilder.toString());

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }

                }
                return jsonObject;
            }
        }
    }

}

