package app.com.example.android.popularmovies.views;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import app.com.example.android.popularmovies.webservices.FetchMoviesTask;
import app.com.example.android.popularmovies.adapters.GridViewAdapter;
import app.com.example.android.popularmovies.R;
import app.com.example.android.popularmovies.Utility;
import app.com.example.android.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private int listPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    // Save the poster HTTP Paths and the movie results
    private GridViewAdapter mAdapter;
    private GridView gridview;
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

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_YEAR = 3;
    public static final int COL_MOVIE_RATING = 4;
    public static final int COL_MOVIE_SYNOPSIS = 5;
    public static final int COL_MOVIE_FAV = 6;
    public static final int COL_MOVIE_URL = 7;

    private int LOADER_ID = 0;

    public MoviesFragment() {
    }

    interface Callback {
        void onItemSelected(Uri uri, boolean networkAvailable, ImageView poster);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /*

        When tablets rotate, the currently selected list item needs to be saved.
        When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        so check for that before storing.

        */
        if (listPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, listPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Log.d(LOG_TAG, "Inside onCreateLoader");
        String sortPref = Utility.getPreferredSortSetting(getContext());

        String sort = MovieContract.MovieEntry.COLUMN_TITLE + " DESC";
        Uri movieUri = MovieContract.MovieEntry.buildUriWithSortSetting(sortPref);

        return new CursorLoader(getActivity(),
                                    movieUri,
                                    MOVIE_PROJECTION_COLUMNS,
                                    null,
                                    null,
                                    sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Log.d(LOG_TAG, "onLoadFinished");
        if (cursor == null) {
            if (!isNetworkAvailable()) {

            }
        } else {
            mAdapter.swapCursor(cursor);
            if (listPosition != ListView.INVALID_POSITION) {
                gridview.smoothScrollToPosition(listPosition);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAdapter = new GridViewAdapter(getContext(), null, 0);
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Prevents the app from crashing if network not available
        if (!isNetworkAvailable()) {
            Context context = getContext();
            CharSequence text = "Network Not Available!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        gridview = (GridView) rootView.findViewById(R.id.gridView);
        gridview.setAdapter(mAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    cursor.moveToFirst();
                    for (int i = 0; i < position; ++i) {
                        cursor.moveToNext();
                    }

                    String sortSetting = Utility.getPreferredSortSetting(getContext());
                    ImageView poster = (ImageView) view.findViewById(R.id.movie_image);
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildUriWithId(cursor.getLong(COL_ID)), isNetworkAvailable(), poster);
                }
                listPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            listPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }


    private void updateMovies() {
        // Get the Preference settings Popular is default setting
        String sortPref = Utility.getPreferredSortSetting(getContext());
        if (!sortPref.equals("favorite")) {
            FetchMoviesTask task = new FetchMoviesTask(getContext());
            task.execute(sortPref);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNetworkAvailable())
            updateMovies();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


