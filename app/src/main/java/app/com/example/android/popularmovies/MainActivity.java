// TODO - remove api key from gradle.properties before uploading to git
package app.com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MoviesFragment.Callback {

    private String movieSetting;
    private final String DETAILFRAGMENTTAG = "DFTAG";
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movieSetting = Utility.getPreferredSortSetting(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        if (findViewById(R.id.detailview) != null) {
            twoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailview, new DetailFragment(), DETAILFRAGMENTTAG)
                    .commit();
        }  else {
            twoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        String setting = Utility.getPreferredSortSetting(this);
        Timber.d(setting);
        if (setting != null && !setting.equals(movieSetting)) {
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENTTAG);
            if (df != null) {
                df.onSettingChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri uri, boolean networkAvailable) {
        if (twoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, uri);
            args.putBoolean(DetailFragment.NETWORK_KEY, networkAvailable);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailview, fragment, DETAILFRAGMENTTAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailView.class)
                    .setData(uri);
            intent.putExtra(DetailFragment.NETWORK_KEY, networkAvailable);
            startActivity(intent);
        }
    }
}
