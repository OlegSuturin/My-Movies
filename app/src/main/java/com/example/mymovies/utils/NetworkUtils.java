package com.example.mymovies.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutionException;

//вся работа с сетью
//API key 3bcb031a3c3fcdb49409692e8fb88be9
//Пример запроса - https://api.themoviedb.org/3/discover/movie?api_key=3bcb031a3c3fcdb49409692e8fb88be9&language=en-US&sort_by=vote_average.desc&include_adult=false&include_video=false&page=2
//Пример пути к картинке https://image.tmdb.org/t/p/w500/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg

//Пример запроса на трейлероы https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key=3bcb031a3c3fcdb49409692e8fb88be9&language=en-US
//Пример запроса на отзывы https://api.themoviedb.org/3/movie/{movie_id}/reviews?api_key=3bcb031a3c3fcdb49409692e8fb88be9&language=en-US&page=1

public class NetworkUtils {
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";    //%s - id фильма
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";     //%s - id фильма


    private static final String PARAMS_API_KEY = "api_key";   //параметры
    private static final String PERAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";   //параметр фильтрации по количеству голосов


    private static final String API_KEY = "3bcb031a3c3fcdb49409692e8fb88be9";     //их значения
    private static final String LANGUAGE_VALUE = "ru-RU";
    private static final String SORT_BY_POPULARITY = "popularity.desc";  // сорт.по популярности убыв.
    private static final String SORT_BY_TOP_RATED = "vote_average.desc"; //по средней оценке убыв.
    private static final String MIN_VOTE_COUNT = "1000";                    //минимальное количество голосов


    public static final int POPULARITY = 0; //для метода- который принимает int, в зависимости от вида сортировки выдает разные результаты
    public static final int TOP_RATED = 1;


    //МЕТОД, ФОРМИРУЕС СТРОКУ ЗАПРОСА URL на ОТЗЫВЫ фильма
    public static URL buildURLReviews(int id) {
        URL resultURL = null;
        String baseURLVideos = String.format(BASE_URL_REVIEWS, id);

        Uri uri = Uri.parse(baseURLVideos).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
            //    .appendQueryParameter(PERAMS_LANGUAGE, LANGUAGE_VALUE)
                .build();

        try {
            resultURL = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return resultURL;
    }

    //МЕТОД, ФОРМИРУЕС СТРОКУ ЗАПРОСА URL на трейлеры фильма
    public static URL buildURLVideos(int id) {
        URL resultURL = null;
        String baseURLVideos = String.format(BASE_URL_VIDEOS, id);

        Uri uri = Uri.parse(baseURLVideos).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PERAMS_LANGUAGE, LANGUAGE_VALUE)
                .build();

        try {
            resultURL = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return resultURL;
    }


    //МЕТОД, ФОРМИРУЕС СТРОКУ ЗАПРОСА URL на фильмы
    public static URL buildURL(int sortBy, int page) {
        URL resultURL = null;   // присв null, т.к. преобразование может выбросить исключение
        String methodSortBy;

        if (sortBy == POPULARITY) {             //проверяем - какой вид сортировки выбран
            methodSortBy = SORT_BY_POPULARITY;
        } else {
            methodSortBy = SORT_BY_TOP_RATED;
        }
        //формируем строку запроса ? = & вст. автоматически
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)  //добпвляем параметры
                .appendQueryParameter(PERAMS_LANGUAGE, LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT_BY, methodSortBy)     //установили метод сортировки
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT)
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

    //МЕТОД - ЗАГРУЗКА ДАННЫХ ИЗ ИНТЕРНЕТА для Трейлеров - исполняется в др.программном потоке
    public static JSONObject getJSONFForVideos(int id) {
        JSONObject jsonObject = null;
        URL url = buildURLVideos(id); //формируем url
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

    //МЕТОД - ЗАГРУЗКА ДАННЫХ ИЗ ИНТЕРНЕТА для ОТЗЫВОВ - исполняется в др.программном потоке
    public static JSONObject getJSONForReviews(int id) {
        JSONObject jsonObject = null;
        URL url = buildURLReviews(id); //формируем url
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

    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {                        //ЗМЕНЕН классом JSONLoader - см.ниже

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
//----------------------------------------------загрузка данных на основе AsyncTaskLoader
    public static class JSONLoader extends AsyncTaskLoader<JSONObject>{             // в <> возвращаемое значение
    private Bundle bundle;    //url передается в объекте Bundle, который формируется при сохоанении состоянии активности
    private OnStartLoadingListener onStartLoadingListener;   //объект интерфейсного типа - Слушатель

    public interface OnStartLoadingListener{           // СЛУШАТЕЛЬ на начало загрузки дпнных
        void onStartLoading();
    }
                                                        //+сеттер на него
    public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
        this.onStartLoadingListener = onStartLoadingListener;
    }

    public JSONLoader(@NonNull Context context, Bundle bundle) {                   //обязательный конструктор
        super(context);
        this.bundle =bundle;                //передали bundle в конструкторе
    }

    @Override                                       //переопределяем
    protected void onStartLoading() {
        super.onStartLoading();
        if(onStartLoadingListener !=null ){                 //РАЗМЕЩЕНИЕ слушателя
            onStartLoadingListener.onStartLoading();
        }
        forceLoad();       // продолжить ЗАГРУЗКУ
    }

    @Nullable
    @Override
    public JSONObject loadInBackground() {
        if (bundle == null){
            return null;
        }
        String urlAsString = bundle.getString("url");  //получаем из bundle сохраненный url по ключу
        URL url = null;

        try {
            url = new URL(urlAsString);
           // Log.i("!@#", url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = null;

        if (url == null) {            //обязательно проверять URL
            return jsonObject; // будет = null
        } else {
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
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
////////////-----------------------------------------------
}

