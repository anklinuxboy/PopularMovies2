package app.com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.example.android.popularmovies.data.MovieContract;
import timber.log.Timber;

/**
 * Created by ankit on 9/27/16.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int DETAIL_LOADER_ID = 1;
    private Map<String, String> movieTrailers;
    private ArrayAdapter<String> reviewAdapter;
    private ArrayAdapter<String> trailerAdapter;
    private ListView listViewReviews;
    private ListView trailerList;
    private static final String[] MOVIE_PROJECTION_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_YEAR,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_FAVORITE,
            MovieContract.MovieEntry.COLUMN_SORT_SETTING,
            MovieContract.MovieEntry.COLUMN_IMAGE_URL

    };

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private String mMovieStr;
    private boolean isNetwork = false;
    private List<String> trailerTitles;
    private List<String> movieReviews;
    private ShareActionProvider actionProvider;
    private boolean loadFinished = false;

    public DetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        //for crate home button
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // The detail Activity called via intent.
        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getData() == null) {
            return null;
        }

        if (intent != null) {
            mMovieStr = intent.getDataString();
            isNetwork = intent.getExtras().getBoolean("isNetwork");
        }

        listViewReviews = (ListView) rootView.findViewById(R.id.review_list_view);
        trailerList = (ListView) rootView.findViewById(R.id.trailer_list_view);
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.share_item);
        actionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (loadFinished) {
            actionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + movieTrailers.get(trailerTitles.get(0)));
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieUri = Uri.parse(mMovieStr);
        String movieID = movieUri.getLastPathSegment();
        //Log.d(LOG_TAG, "last path segment " + movieID);
        Uri newMovieUri = MovieContract.MovieEntry.buildUriWithId(Long.parseLong(movieID));
        //Log.d(LOG_TAG, "movie uri " + movieUri);
        return new CursorLoader(getActivity(),
                newMovieUri,
                MOVIE_PROJECTION_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        if (cursor != null) {
            cursor.moveToFirst();
            //Log.d(LOG_TAG, "cursor ID on load finished " + cursor.getLong(0));
            //Log.d(LOG_TAG, "cursor count " + cursor.getCount());
            TextView rating = (TextView) getView().findViewById(R.id.rating);
            TextView release = (TextView) getView().findViewById(R.id.release);
            TextView plot = (TextView) getView().findViewById(R.id.plot);
            ImageView imageView = (ImageView) getView().findViewById(R.id.poster);
            TextView title = (TextView) getView().findViewById(R.id.title1);

            CheckBox checkBox = (CheckBox) getView().findViewById(R.id.star);

            checkBox.setChecked(cursor.getString(MoviesFragment.COL_MOVIE_FAV).equals("1"));

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = ((CheckBox)v).isChecked();
                    String selection = MovieContract.MovieEntry._ID + " = ? ";
                    String[] selectionArgs = new String[]{Long.toString(cursor.getLong(MoviesFragment.COL_ID))};
                    if (checked) {
                        //Log.d(LOG_TAG, "onClick checked");
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, "1");
                        getContext().getContentResolver().update(MovieContract.CONTENT_URI, values, selection, selectionArgs);
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, "0");
                        getContext().getContentResolver().update(MovieContract.CONTENT_URI, values, selection, selectionArgs);
                    }
                }
            });

            title.setTextColor(Color.WHITE);
            title.setText(cursor.getString(MoviesFragment.COL_MOVIE_TITLE));

            release.setText(cursor.getString(MoviesFragment.COL_MOVIE_YEAR));

            rating.setText(cursor.getString(MoviesFragment.COL_MOVIE_RATING));

            plot.setText(cursor.getString(MoviesFragment.COL_MOVIE_SYNOPSIS));

            String id = cursor.getString(MoviesFragment.COL_MOVIE_ID);

            Picasso.with(getContext())
                    .load(cursor.getString(MoviesFragment.COL_MOVIE_URL))
                    .fit()
                    .into(imageView);

            if (isNetwork) {
                getTrailersReviews(id);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    public void getTrailersReviews(String movieId) {
        FetchMovieTrailers fetchMovieTrailers = new FetchMovieTrailers();
        FetchMovieReviews fetchMovieReviews = new FetchMovieReviews();
        fetchMovieTrailers.execute(movieId);
        fetchMovieReviews.execute(movieId);
    }

    public class FetchMovieTrailers extends AsyncTask<String, Void, Void> {

        public FetchMovieTrailers() {
            movieTrailers = new HashMap<>();
            trailerTitles = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(String... params) {
            String movieId = params[0];
            HttpURLConnection trailerUrlConnection = null;
            BufferedReader reader = null;

            String movieTrailerJSONRaw = null;

            try {
                Uri.Builder trailerBuilder = new Uri.Builder();
                trailerBuilder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(movieId)
                        .appendPath("videos")
                        .appendQueryParameter("api_key", BuildConfig.OPEN_TMDB_API_KEY);



                URL trailerUrl = new URL(trailerBuilder.build().toString());

                trailerUrlConnection = (HttpURLConnection) trailerUrl.openConnection();

                // set the method of request and connect
                trailerUrlConnection.setRequestMethod("GET");
                trailerUrlConnection.setDoInput(true);

                trailerUrlConnection.connect();
                // Read input stream into a string
                InputStream tinputStream = trailerUrlConnection.getInputStream();

                StringBuffer tbuffer = new StringBuffer();
                if (tinputStream == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(tinputStream));

                String line;
                while ((line = reader.readLine()) != null)
                    tbuffer.append(line + "\n");

                if (tbuffer.length() == 0)
                    return null;

                movieTrailerJSONRaw = tbuffer.toString();
                getTrailerData(movieTrailerJSONRaw);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                // close the connection and reader.
                if (trailerUrlConnection != null)
                    trailerUrlConnection.disconnect();

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

        @Override
        protected void onPostExecute(Void nothing) {
            trailerAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.detail_trailers, R.id.trailer_num,
                    trailerTitles);
            trailerAdapter.notifyDataSetChanged();
            trailerList.setAdapter(trailerAdapter);
            Utility.setListViewHeightBasedOnItems(trailerList);
            loadFinished = true;
            if (actionProvider != null) {
                actionProvider.setShareIntent(createShareIntent());
            }
            trailerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String key = (String) trailerList.getItemAtPosition(position);
                    String value = movieTrailers.get(key);
                    startYouTubeVideo(value);
                }
            });
        }

        private void getTrailerData(String movieTrailerJSONRaw) throws JSONException {
            final String TRAILER_RESULT = "results";
            final String TRAILER_NAME = "name";
            final String TRAILER_KEY = "key";

            JSONObject trailersObj = new JSONObject(movieTrailerJSONRaw);
            JSONArray resultArr = trailersObj.getJSONArray(TRAILER_RESULT);

            for (int i = 0; i < resultArr.length(); ++i) {
                String trailerName;
                String trailerLink;
                JSONObject trailer = resultArr.getJSONObject(i);
                trailerName = trailer.getString(TRAILER_NAME);
                trailerLink = trailer.getString(TRAILER_KEY);
                trailerTitles.add(trailerName);
                movieTrailers.put(trailerName, trailerLink);
            }
        }
    }

    private void startYouTubeVideo(String value) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + value));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + value));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    public class FetchMovieReviews extends AsyncTask<String, Void, Void> {

        public FetchMovieReviews() {
            movieReviews = new ArrayList<>();
        }
        @Override
        protected Void doInBackground(String... params) {
            String movieId = params[0];
            
            HttpURLConnection reviewsUrlConnection = null;
            BufferedReader reader = null;
            String movieReviewJSONRaw = null;

            try {
                Uri.Builder reviewBuilder = new Uri.Builder();
                reviewBuilder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(movieId)
                        .appendPath("reviews")
                        .appendQueryParameter("api_key", BuildConfig.OPEN_TMDB_API_KEY);


                URL reviewsUrl = new URL(reviewBuilder.build().toString());

                reviewsUrlConnection = (HttpURLConnection) reviewsUrl.openConnection();

                // set the method of request and connect
                reviewsUrlConnection.setRequestMethod("GET");
                reviewsUrlConnection.setDoInput(true);

                reviewsUrlConnection.connect();
                // Read input stream into a string
                InputStream inputStream = reviewsUrlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                    return null;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");

                if (buffer.length() == 0)
                    return null;

                movieReviewJSONRaw = buffer.toString();
                getReviewData(movieReviewJSONRaw);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                // close the connection and reader.
                if (reviewsUrlConnection != null)
                    reviewsUrlConnection.disconnect();

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

        @Override
        protected void onPostExecute(Void nothing) {
            reviewAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, movieReviews);
            listViewReviews.setVisibility(View.VISIBLE);
            listViewReviews.setAdapter(reviewAdapter);
            reviewAdapter.notifyDataSetChanged();
        }

        private void getReviewData(String movieReviewJSONRaw) throws JSONException {
            final String REVIEW_RESULT = "results";
            final String REVIEW_CONTENT = "content";

            JSONObject reviewResults = new JSONObject(movieReviewJSONRaw);
            JSONArray reviews = reviewResults.getJSONArray(REVIEW_RESULT);

            for (int i = 0; i < reviews.length(); ++i) {
                String review;
                JSONObject movieReview = reviews.getJSONObject(i);
                review = movieReview.getString(REVIEW_CONTENT);
                movieReviews.add(review);
            }
        }
    }

}
