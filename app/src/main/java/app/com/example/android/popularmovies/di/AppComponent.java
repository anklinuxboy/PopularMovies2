package app.com.example.android.popularmovies.di;

import javax.inject.Singleton;

import app.com.example.android.popularmovies.viewmodels.MovieFragmentViewModel;
import app.com.example.android.popularmovies.views.DetailFragment;
import app.com.example.android.popularmovies.views.MoviesFragment;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class, DbModule.class})
public interface AppComponent {
    void inject(MovieFragmentViewModel movieFragmentViewModel);
    void inject(DetailFragment detailFragment);
    void inject(MoviesFragment moviesFragment);
}
