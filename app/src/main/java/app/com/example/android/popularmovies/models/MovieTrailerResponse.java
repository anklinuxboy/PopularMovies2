package app.com.example.android.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieTrailerResponse {
    @SerializedName("results")
    private List<MovieTrailers> movieTrailers;

    public List<MovieTrailers> getMovieTrailers() {
        return movieTrailers;
    }

    public void setMovieTrailers(List<MovieTrailers> movieTrailers) {
        this.movieTrailers = movieTrailers;
    }
}
