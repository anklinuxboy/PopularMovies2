package app.com.example.android.popularmovies.views;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.com.example.android.popularmovies.MoviesApplication;
import app.com.example.android.popularmovies.R;
import app.com.example.android.popularmovies.Utility;
import app.com.example.android.popularmovies.adapters.GridViewAdapter;
import app.com.example.android.popularmovies.data.MovieDatabase;
import app.com.example.android.popularmovies.models.MovieInfo;
import app.com.example.android.popularmovies.observers.MovieFragmentObserver;
import app.com.example.android.popularmovies.viewmodels.MovieFragmentViewModel;
import timber.log.Timber;

public class MoviesFragment extends Fragment {

    @Inject
    MovieDatabase movieDatabase;

    private int listPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    // Save the poster HTTP Paths and the movie results
    private GridViewAdapter mAdapter;
    private GridView gridview;
    private MovieFragmentViewModel viewModel;
    private List<MovieInfo> movies = new ArrayList<>();

    public MoviesFragment() {
    }

    interface Callback {
        void onItemSelected(Uri uri, boolean networkAvailable, ImageView poster);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MoviesApplication) getActivity().getApplication()).getAppComponent().inject(this);
        getLifecycle().addObserver(new MovieFragmentObserver());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("test oncreateview");
        mAdapter = new GridViewAdapter(getContext(), movies);
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

        viewModel = ViewModelProviders.of(getActivity()).get(MovieFragmentViewModel.class);
        viewModel.loadMovies();
        viewModel.getMovieInfo().observe(this, movieInfos -> {
            this.movies.clear();
            if (movieInfos != null) {
                this.movies.addAll(movieInfos);
                mAdapter.notifyDataSetChanged();
            }
        });

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
//                    ((Callback) getActivity())
//                            .onItemSelected(MovieContract.MovieEntry.buildUriWithId(cursor.getLong(COL_ID)), isNetworkAvailable(), poster);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


