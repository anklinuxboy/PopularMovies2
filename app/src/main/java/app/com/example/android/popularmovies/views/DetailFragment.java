package app.com.example.android.popularmovies.views;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
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
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import app.com.example.android.popularmovies.BuildConfig;
import app.com.example.android.popularmovies.CustomTextDrawable;
import app.com.example.android.popularmovies.MoviesApplication;
import app.com.example.android.popularmovies.R;
import app.com.example.android.popularmovies.Utility;
import app.com.example.android.popularmovies.data.MovieContract;
import app.com.example.android.popularmovies.models.MovieReviewResponse;
import app.com.example.android.popularmovies.models.MovieReviews;
import app.com.example.android.popularmovies.models.MovieTrailerResponse;
import app.com.example.android.popularmovies.models.MovieTrailers;
import app.com.example.android.popularmovies.webservices.MovieService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Inject
    MovieService movieService;

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
    public static final String DETAIL_URI = "URI";
    public static final String NETWORK_KEY = "isNetwork";
    private Uri movieUri;
    private boolean isNetwork = false;
    private List<String> trailerTitles;
    private List<String> movieReviews;
    private ShareActionProvider actionProvider;
    private boolean loadFinished = false;

    public DetailFragment() {
        movieTrailers = new HashMap<>();
        movieReviews = new ArrayList<>();
        trailerTitles = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments != null) {
            movieUri = arguments.getParcelable(DETAIL_URI);
            isNetwork = arguments.getBoolean(NETWORK_KEY);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_detail);

        //for crate home button
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ((MoviesApplication)(getActivity().getApplication())).getAppComponent().inject(this);

        listViewReviews = rootView.findViewById(R.id.review_list_view);
        trailerList = rootView.findViewById(R.id.trailer_list_view);
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);

        return rootView;
    }

    public void onSettingChanged() {
        getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
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
        if (movieUri != null) {
            String movieID = movieUri.getLastPathSegment();
            Uri newMovieUri = MovieContract.MovieEntry.buildUriWithId(Long.parseLong(movieID));
            //Log.d(LOG_TAG, "movie uri " + movieUri);
            return new CursorLoader(getActivity(),
                    newMovieUri,
                    MOVIE_PROJECTION_COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
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
            ImageView emptyView = (ImageView) getView().findViewById(R.id.empty_view);
            emptyView.setVisibility(View.VISIBLE);

            checkBox.setVisibility(View.VISIBLE);

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
                    .placeholder(new CustomTextDrawable(cursor.getString(MoviesFragment.COL_MOVIE_TITLE)))
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
        getMovieTrailers(movieId);
        getMovieReviews(movieId);
    }

    private void getMovieReviews(String movieId) {
        Call<MovieReviewResponse> reviewResponseCall = movieService.getMovieReviews(movieId, BuildConfig.OPEN_TMDB_API_KEY);

        reviewResponseCall.enqueue(new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                for (MovieReviews review : response.body().getMovieReviews()) {
                    movieReviews.add(review.getReview());
                }

                reviewAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, movieReviews);
                listViewReviews.setVisibility(View.VISIBLE);
                listViewReviews.setAdapter(reviewAdapter);
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable t) {

            }
        });
    }

    private void getMovieTrailers(String movieID) {
        Call<MovieTrailerResponse> trailerResponseCall = movieService.getMovieTrailers(movieID, BuildConfig.OPEN_TMDB_API_KEY);

        trailerResponseCall.enqueue(new Callback<MovieTrailerResponse>() {
            @Override
            public void onResponse(Call<MovieTrailerResponse> call, Response<MovieTrailerResponse> response) {
                for (MovieTrailers trailer : response.body().getMovieTrailers()) {
                    trailerTitles.add(trailer.getTrailerName());
                    movieTrailers.put(trailer.getTrailerName(), trailer.getTrailerKey());
                }
                trailerAdapter = new ArrayAdapter<>(getActivity(),
                        R.layout.detail_trailers, R.id.trailer_num,
                        trailerTitles);
                trailerAdapter.notifyDataSetChanged();
                trailerList.setAdapter(trailerAdapter);
                Utility.setListViewHeightBasedOnItems(trailerList);
                loadFinished = true;
                if (null != actionProvider && trailerTitles.size() != 0) {
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

            @Override
            public void onFailure(Call<MovieTrailerResponse> call, Throwable t) {

            }
        });
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
}
