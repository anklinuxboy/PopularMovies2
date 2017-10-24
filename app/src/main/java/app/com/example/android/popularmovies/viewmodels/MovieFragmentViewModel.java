package app.com.example.android.popularmovies.viewmodels;

import android.arch.lifecycle.ViewModel;

import app.com.example.android.popularmovies.models.MovieInfo;

public class MovieFragmentViewModel extends ViewModel {
    private MovieInfo movieInfo;

    public MovieInfo getMovieInfo() {
        return movieInfo;
    }
}
