package app.com.example.android.popularmovies;

import android.net.Uri;
import android.test.AndroidTestCase;

import app.com.example.android.popularmovies.data.MovieContract;

/**
 * Created by ankit on 9/4/16.
 */
public class TestMovieContract extends AndroidTestCase {
    private final String sortSetting = "/Highest Rating";

    public void testBuildUriWithSortSetting() {
        Uri sortUri = MovieContract.MovieEntry.buildUriWithSortSetting(sortSetting);

        assertNotNull("Non null uri returned", sortUri);

        assertEquals("NOt appended correctly", sortSetting, sortUri.getLastPathSegment());

    }
}
