package app.com.example.android.popularmovies.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import app.com.example.android.popularmovies.data.MovieDatabase;
import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {

    public DbModule() {}

    @Provides
    @Singleton
    public MovieDatabase provideMovieDB(Application application) {
        return Room.databaseBuilder(application.getApplicationContext(),
                MovieDatabase.class, "movies").build();
    }

}
