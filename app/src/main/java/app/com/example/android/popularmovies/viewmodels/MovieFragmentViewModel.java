package app.com.example.android.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Observable;

import javax.inject.Inject;

import app.com.example.android.popularmovies.MoviesApplication;
import app.com.example.android.popularmovies.Utility;
import app.com.example.android.popularmovies.models.MovieInfo;
import app.com.example.android.popularmovies.repositories.MoviesRepository;
import app.com.example.android.popularmovies.webservices.MovieService;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MovieFragmentViewModel extends AndroidViewModel {
    @Inject
    MovieService movieService;
    private Application application;

    private MutableLiveData<List<MovieInfo>> movieInfo;
    private MoviesRepository repository;

    public MovieFragmentViewModel(Application application) {
        super(application);
        this.application = application;
        ((MoviesApplication) application).getAppComponent().inject(this);
        repository = new MoviesRepository(application);
    }

    public LiveData<List<MovieInfo>> getMovieInfo() {
        if (movieInfo == null) {
            movieInfo = new MutableLiveData<>();
        }
        return movieInfo;
    }

    public void loadMovies() {
        // Get the Preference settings Popular is default setting
        String sortPref = Utility.getPreferredSortSetting(application);
        repository.getMovies(sortPref)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> movieInfo.setValue(repository.getMoviesLiveData().getValue()))
                .subscribe();
    }
}
