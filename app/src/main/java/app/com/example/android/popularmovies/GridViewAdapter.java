package app.com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.zip.Inflater;

/**
 * Created by ankit on 3/6/16.
 */
public class GridViewAdapter extends CursorAdapter {

    public GridViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String getPosterUrlFromCursor(Cursor cursor) {
        return cursor.getString(MoviesFragment.COL_MOVIE_URL);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_grid, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView;

        imageView = (ImageView) view.findViewById(R.id.movie_image);

        Picasso.with(context)
                .load(getPosterUrlFromCursor(cursor))
                .into(imageView);
    }
}


