package app.com.example.android.popularmovies.webservices;

import java.util.List;

import app.com.example.android.popularmovies.models.MovieInfo;
import app.com.example.android.popularmovies.models.MovieReviews;
import app.com.example.android.popularmovies.models.MovieTrailers;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieService {
    @GET("movie/{sortPref}")
    Call<List<MovieInfo>> getMovies(@Path("sortPref") String sortPref, @Query("api_key") String key);

    @GET("movie/{movieID}/videos")
    Call<List<MovieTrailers>> getMovieTrailers(@Path("movieID") String movieID, @Query("api_key") String key);

    @GET("movie/{movieID}/reviews")
    Call<List<MovieReviews>> getMovieReviews(@Path("movieID") String movieID, @Query("api_key") String key);
}
