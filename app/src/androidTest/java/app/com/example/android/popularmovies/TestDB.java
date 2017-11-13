package app.com.example.android.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import app.com.example.android.popularmovies.data.MovieContract;
import app.com.example.android.popularmovies.data.MovieDBHelper;

/**
 * Created by ankit on 9/4/16.
 */
public class TestDB extends AndroidTestCase {

    public void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
    }

    public void setup() {
        deleteTheDatabase();
    }

    /*
     * Test if DB is created succesfully
     */
    public void testCreateDB() throws Throwable {
        final HashSet<String> tableName = new HashSet<String>();
        tableName.add(MovieContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDBHelper(this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableName.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("DB not created correctly", tableName.isEmpty());

        final HashSet<String> movieColumns = new HashSet<String>();
        movieColumns.add(MovieContract.MovieEntry._ID);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_FAVORITE);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_IMAGE_URL);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_RATING);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_SORT_SETTING);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumns.add(MovieContract.MovieEntry.COLUMN_YEAR);

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumns.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                movieColumns.isEmpty());
        c.close();
        db.close();
    }

    public void testMovieDB() {
        MovieDBHelper helper = new MovieDBHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();

        long rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue("Not true", rowId != -1);

        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Incorrect insertion", cursor, movieValues);
        cursor.close();
        db.close();
        deleteTheDatabase();
    }
}
