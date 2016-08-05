package app.com.example.android.popularmovies;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Created by ankit on 8/5/16.
 */
public class DetailView extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);

        // Get the Parcelable intent
        Intent intent = getIntent();
        MovieInfo movie = intent.getExtras().getParcelable("movie");

        TextView rating = (TextView) findViewById(R.id.rating);
        TextView release = (TextView) findViewById(R.id.release);
        TextView plot = (TextView) findViewById(R.id.plot);
        ImageView imageView = (ImageView) findViewById(R.id.poster);
        TextView title = (TextView) findViewById(R.id.title1);

        title.setTextColor(Color.WHITE);
        title.setText(movie.getTitle());

        release.setText(movie.getRelease());

        rating.setText(movie.getRating());

        plot.setText(movie.getPlot());

        String url = movie.getUrl();

        Picasso.with(getApplicationContext())
                .load(url)
                .fit()
                .into(imageView);
    }
}
