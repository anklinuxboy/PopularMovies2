package app.com.example.android.popularmovies.di;

import javax.inject.Singleton;

import app.com.example.android.popularmovies.views.DetailFragment;
import app.com.example.android.popularmovies.views.MoviesFragment;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetModule.class})
public interface AppComponent {
    void inject(MoviesFragment moviesFragment);
    void inject(DetailFragment detailFragment);
}
