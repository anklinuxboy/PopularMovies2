package app.com.example.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import app.com.example.android.popularmovies.data.MovieContract;

/**
 * Created by ankit on 9/4/16.
 */
public class TestUtilities extends AndroidTestCase {

    /*
     * Create test movie values
     */
    static ContentValues createMovieValues() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, "0");
        values.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, "www.movie.org");
        values.put(MovieContract.MovieEntry.COLUMN_RATING, "4.5/10");
        values.put(MovieContract.MovieEntry.COLUMN_SORT_SETTING, "Highest Rating");
        values.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "This movie is about a hero");
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, "Hero");
        values.put(MovieContract.MovieEntry.COLUMN_YEAR, "2014");
        return values;
    }

    /*
     * Check if values returned and expected are equal
     */
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
