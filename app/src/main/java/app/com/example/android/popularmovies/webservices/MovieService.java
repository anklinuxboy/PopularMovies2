package app.com.example.android.popularmovies.webservices;

import app.com.example.android.popularmovies.models.MovieReviewResponse;
import app.com.example.android.popularmovies.models.MovieTrailerResponse;
import app.com.example.android.popularmovies.models.MoviesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {
    @GET("movie/{sortPref}")
    Call<MoviesResponse> getMovies(@Path("sortPref") String sortPref, @Query("api_key") String key);

    @GET("movie/{movieID}/videos")
    Call<MovieTrailerResponse> getMovieTrailers(@Path("movieID") String movieID, @Query("api_key") String key);

    @GET("movie/{movieID}/reviews")
    Call<MovieReviewResponse> getMovieReviews(@Path("movieID") String movieID, @Query("api_key") String key);
}
