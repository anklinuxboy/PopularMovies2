package app.com.example.android.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesResponse {
    @SerializedName("results")
    private List<MovieInfo> movies;

    public List<MovieInfo> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieInfo> movies) {
        this.movies = movies;
    }
}
