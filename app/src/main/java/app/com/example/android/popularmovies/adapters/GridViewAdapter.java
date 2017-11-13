package app.com.example.android.popularmovies.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.com.example.android.popularmovies.CustomTextDrawable;
import app.com.example.android.popularmovies.R;
import app.com.example.android.popularmovies.models.MovieInfo;
import timber.log.Timber;

public class GridViewAdapter extends BaseAdapter {

    private List<MovieInfo> movies;
    private Context context;

    public GridViewAdapter(Context context, List<MovieInfo> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieInfo movie = movies.get(position);
        ImageView imageView;
        View view = convertView;
        if (view == null) {
            view = View.inflate(context, R.layout.fragment_grid, null);
            imageView = view.findViewById(R.id.movie_image);
        } else {
            imageView = view.findViewById(R.id.movie_image);
        }

        Picasso.with(context)
                .load(movie.getPosterUrl())
                .placeholder(new CustomTextDrawable(movie.getTitle()))
                .into(imageView);
        return view;
    }
}


