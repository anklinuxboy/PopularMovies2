package app.com.example.android.popularmovies.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import app.com.example.android.popularmovies.models.MovieInfo;

@Database(entities = {MovieInfo.class}, version = 4)
public abstract class MovieDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();
}
