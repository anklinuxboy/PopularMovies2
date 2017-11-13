package app.com.example.android.popularmovies.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.inject.Inject;

import app.com.example.android.popularmovies.BuildConfig;
import app.com.example.android.popularmovies.MoviesApplication;
import app.com.example.android.popularmovies.data.MovieDatabase;
import app.com.example.android.popularmovies.models.MovieInfo;
import app.com.example.android.popularmovies.models.MoviesResponse;
import app.com.example.android.popularmovies.webservices.MovieService;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class MoviesRepository {

    @Inject
    MovieService movieService;
    @Inject
    MovieDatabase movieDatabase;

    private List<MovieInfo> moviesList;

    private LiveData<List<MovieInfo>> movies;

    public MoviesRepository(Application application) {
        ((MoviesApplication) application).getAppComponent().inject(this);
        movies = new MutableLiveData<>();
        moviesList = new ArrayList<>();
    }

    public Completable getMovies(String sortPref) {
        Timber.d("test repo getmovies");
        if (!sortPref.equals("favorite")) {

            Single<MoviesResponse> movieFlowable = movieService.getMovies(sortPref, BuildConfig.OPEN_TMDB_API_KEY);

            return movieFlowable.subscribeOn(Schedulers.io())
                    .flatMapCompletable(moviesResponse -> {
                            for (MovieInfo movie : moviesResponse.getMovies()) {
                            movie.setPosterUrl("http://image.tmdb.org/t/p/w185/" + movie.getPosterPath());
                            movie.setSortSetting(sortPref);
                            movie.setVoterRating(movie.getRating() + "/10");
                        }

                        return insertMovies(moviesResponse.getMovies()).andThen(getMoviesFromDB(sortPref));
                    });
        } else {
            return getMoviesFromDB(sortPref);
        }
    }

    public LiveData<List<MovieInfo>> getMoviesLiveData() {
        return movies;
    }

    private Completable getMoviesFromDB(String sortPref) {
        Timber.d("test get movies from db");
        return Completable.fromAction(() -> movies = movieDatabase.getMovieDao().getMoviesBySortSetting(sortPref))
                .subscribeOn(Schedulers.io());
    }

    private Completable insertMovies(List<MovieInfo> movies) {
        Timber.d("test insert movies in db");
        return Completable.fromAction(() -> movieDatabase.getMovieDao().insertAll(movies)).subscribeOn(Schedulers.io());
    }
}
