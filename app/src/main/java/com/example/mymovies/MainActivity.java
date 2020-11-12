package com.example.mymovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mymovies.data.Movie;
import com.example.mymovies.utils.JSONUtils;
import com.example.mymovies.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(NetworkUtils.POPULARITY, 2);  //
        if (jsonObject == null){
            Toast.makeText(this, "Произошла ошибка при загрузке данных из сети", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Данные загружены успешно", Toast.LENGTH_SHORT).show();
            ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
            StringBuilder builder = new StringBuilder();
            for (Movie movie: movies){
                builder.append(movie.getTitle()).append("\n");
            }
            Log.i("!@#", builder.toString());
        }
        
    }
}