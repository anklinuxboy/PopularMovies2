package app.com.example.android.popularmovies.viewmodels;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import app.com.example.android.popularmovies.models.MovieInfo;

public class MovieFragmentViewModel extends ViewModel implements LifecycleObserver {
    private MutableLiveData<List<MovieInfo>> movieInfo;

    public MutableLiveData<List<MovieInfo>> getMovieInfo() {
        if (movieInfo == null) {
            movieInfo = new MutableLiveData<>();
        }

        return movieInfo;
    }
}
