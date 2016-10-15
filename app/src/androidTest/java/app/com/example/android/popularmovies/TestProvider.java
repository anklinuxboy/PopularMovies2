package app.com.example.android.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import app.com.example.android.popularmovies.data.MovieContract;

/**
 * Created by ankit on 9/5/16.
 */
public class TestProvider extends AndroidTestCase {

    public void deleteAllRecords() {
        mContext.getContentResolver().delete(MovieContract.CONTENT_URI, null, null);
        Cursor cursor = mContext.getContentResolver().query(MovieContract.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals("All records not deleted", 0, cursor.getCount());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testInsert() {
        ContentValues values = TestUtilities.createMovieValues();

        Uri movieUri = mContext.getContentResolver().insert(MovieContract.CONTENT_URI, values);

        long locationRowId = ContentUris.parseId(movieUri);

        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        if (cursor != null)
            cursor.moveToFirst();
        TestUtilities.validateCurrentRecord("testInsertReadProvider. Error validating LocationEntry.",
                cursor, values);
    }

}
