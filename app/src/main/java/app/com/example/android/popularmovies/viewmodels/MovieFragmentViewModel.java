package app.com.example.android.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import app.com.example.android.popularmovies.BuildConfig;
import app.com.example.android.popularmovies.MoviesApplication;
import app.com.example.android.popularmovies.Utility;
import app.com.example.android.popularmovies.data.MovieContract;
import app.com.example.android.popularmovies.models.MovieInfo;
import app.com.example.android.popularmovies.models.MoviesResponse;
import app.com.example.android.popularmovies.webservices.MovieService;
import dagger.Provides;
import retrofit2.Call;
import retrofit2.Response;

public class MovieFragmentViewModel extends AndroidViewModel {
    @Inject
    MovieService movieService;
    Application application;

    private MutableLiveData<List<MovieInfo>> movieInfo;

    public MovieFragmentViewModel(Application application) {
        super(application);
        this.application = application;
        ((MoviesApplication)application).getAppComponent().inject(this);
    }

    public MutableLiveData<List<MovieInfo>> getMovieInfo() {
        return movieInfo;
    }

    public void loadMovies() {
        movieInfo = new MutableLiveData<>();
        // Get the Preference settings Popular is default setting
        String sortPref = Utility.getPreferredSortSetting(this.application);
        if (!sortPref.equals("favorite")) {

            Call<MoviesResponse> call = movieService.getMovies(sortPref, BuildConfig.OPEN_TMDB_API_KEY);

            call.enqueue(new retrofit2.Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    movieInfo.setValue(response.body().getMovies());
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Toast.makeText(application.getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
