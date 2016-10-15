package app.com.example.android.popularmovies;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import app.com.example.android.popularmovies.data.MovieContract;

/**
 * Created by ankit on 9/6/16.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private final Context mContext;

    public FetchMoviesTask(Context context) { mContext = context; }

    private boolean DEBUG = true;
    // Return strings of movie data

    private void addMovie(MovieInfo movie) {
        String preference = Utility.getPreferredSortSetting(mContext);

        // check if movie exists in the database
        //Log.d(LOG_TAG, "Inside addMovie");
        String selection = MovieContract.MovieEntry.COLUMN_TITLE + " = ?";
        Cursor cursor = mContext.getContentResolver().query(MovieContract.CONTENT_URI,
                                                        new String[]{MovieContract.MovieEntry.COLUMN_TITLE},
                                                        selection,
                                                        new String[]{movie.getTitle()},
                                                        null);

        // add movie if it doesn't exist in database
        if (cursor.moveToFirst()) {
            cursor.close();
        } else {
            //Log.d(LOG_TAG, "cursor nil, adding movie to db");
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
            values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            values.put(MovieContract.MovieEntry.COLUMN_YEAR, movie.getRelease());
            values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.getPlot());
            values.put(MovieContract.MovieEntry.COLUMN_SORT_SETTING, preference);
            values.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, movie.getUrl());
            values.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getRating());
            mContext.getContentResolver().insert(MovieContract.CONTENT_URI, values);
        }
    }

    private void getMovieDataJson(String moviesJSONRaw) throws JSONException {
        // JSON objects that need to be extracted
        final String MDB_RESULT = "results";
        final String MDB_POSTER = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_TITLE = "title";
        final String MDB_RELEASE = "release_date";
        final String MDB_RATING = "vote_average";
        final String MDB_ID = "id";
        JSONObject moviesJson = new JSONObject(moviesJSONRaw);
        JSONArray results = moviesJson.getJSONArray(MDB_RESULT);

        // base url for movie poster
        final String URL_POSTER = "http://image.tmdb.org/t/p/w185/";

        for (int i = 0; i < results.length(); ++i) {
            String plot;
            String title;
            String release;
            String rating;
            String posterUrl;
            String id;
            // extract all the relevant information from the object
            JSONObject movie = results.getJSONObject(i);
            plot = movie.getString(MDB_OVERVIEW);
            id = movie.getString(MDB_ID);
            posterUrl = URL_POSTER + movie.getString(MDB_POSTER);
            title = movie.getString(MDB_TITLE);
            release = movie.getString(MDB_RELEASE);
            rating = movie.getString(MDB_RATING) + "/10";
            // add all the information in one string for parsing later on
            MovieInfo info = new MovieInfo(posterUrl, plot, title, release, rating, id);
            addMovie(info);
        }
    }

    // Do background work to fetch thread. Network threads are done on background
    @Override
    protected Void doInBackground(String... params) {

        String sortPref = params[0];
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJSONRaw = null;

        // try to open internet connection. Catch IOException.
        try {
            // Build the URI for TMDB
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(sortPref)  //  Popular setting
                    .appendQueryParameter("api_key", BuildConfig.OPEN_TMDB_API_KEY);

            URL url = new URL(builder.build().toString());

            urlConnection = (HttpURLConnection) url.openConnection();

            // set the method of request and connect
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.connect();
            // Read input stream into a string
            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)
                return null;

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line + "\n");

            if (buffer.length() == 0)
                return null;

            moviesJSONRaw = buffer.toString();
            getMovieDataJson(moviesJSONRaw);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            // close the connection and reader.
            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error Closing Stream", e);
                }
            }
        }
        return null;
    }
}
