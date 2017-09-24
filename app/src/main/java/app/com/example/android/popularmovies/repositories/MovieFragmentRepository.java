package app.com.example.android.popularmovies.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import app.com.example.android.popularmovies.models.MoviesResponse;
import app.com.example.android.popularmovies.webservices.MovieService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragmentRepository {
    private MovieService movieService;

    public LiveData<MoviesResponse> getMovies(String sortPref, String key) {
        final MutableLiveData<MoviesResponse> data = new MutableLiveData<>();
        movieService.getMovies(sortPref, key).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {

            }
        });

        return data;
    }
}
