package app.com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
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
public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<String> movieurls = new ArrayList<String>();

    public GridViewAdapter(Context context, int resource, int id, ArrayList<String> urls) {
        super(context, resource, id, urls);
        this.context = context;
        this.movieurls = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_grid, parent, false);
            imageView = (ImageView) convertView.findViewById(R.id.movie_image);
        } else {
            imageView = (ImageView) convertView.findViewById(R.id.movie_image);
        }

        Picasso.with(context)
                .load(movieurls.get(position))
                .into(imageView);

        return convertView;
    }
}


