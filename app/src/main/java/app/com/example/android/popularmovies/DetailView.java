package app.com.example.android.popularmovies;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ankit on 8/5/16.
 */
public class DetailView extends AppCompatActivity {

    private final String LOG_TAG = DetailView.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            arguments.putBoolean(DetailFragment.NETWORK_KEY, getIntent().getBooleanExtra(DetailFragment.NETWORK_KEY, false));

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailview, fragment)
                    .commit();
        }
    }
}
