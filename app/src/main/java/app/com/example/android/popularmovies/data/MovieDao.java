package app.com.example.android.popularmovies.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import app.com.example.android.popularmovies.models.MovieInfo;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies")
    List<MovieInfo> getAll();

    @Query("SELECT * FROM movies where movieId LIKE :uid LIMIT 1")
    MovieInfo findById(String uid);

    @Query("SELECT * FROM movies where title LIKE :title LIMIT 1")


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(MovieInfo... movies);

    @Delete
    void deleteAll(MovieInfo... movies);

    @Update
    void updateMovies(MovieInfo... movies);
}
