package app.com.example.android.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieReviewResponse {
    @SerializedName("results")
    private List<MovieReviews> movieReviews;

    public List<MovieReviews> getMovieReviews() {
        return movieReviews;
    }
}
