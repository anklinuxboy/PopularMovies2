package app.com.example.android.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by ankit on 9/4/16.
 */
public class MovieProvider extends ContentProvider {

    private final String LOG_TAG = MovieProvider.class.getSimpleName();
    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int MOVIE_WITH_SORT_SETTING = 102;
    // Uri matcher
    private static final UriMatcher sMatcher = buildUriMatcher();
    private MovieDBHelper mDBHelper;

    // Creates a new Movie DB helper reference
    @Override
    public boolean onCreate() {
        mDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    /*
     * Selection string for database
     */
    private static final String sSortSettingSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_SORT_SETTING + " = ? ";

    private static final String sFavoritesSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_FAVORITE + " = ? ";

    private static final String sMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry._ID + " = ? ";

    /*
     * Get the movies according to the selections
     */
    private Cursor getMovieWithId(Uri uri, String[] projection, String sortOrder) {
        long rowId = MovieContract.MovieEntry.getRowIdFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieIdSelection;
        selectionArgs = new String[]{Long.toString(rowId)};

        return mDBHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getMovieWithSortSelection(Uri uri, String[] projection, String sortOrder) {
        //Log.d(LOG_TAG, "Inside movie with sort selection");
        String sortSetting = MovieContract.MovieEntry.getSortSettingFromUri(uri);

        String[] selectionArgs;
        String selection;
        String favoriteSetting = "favorite";
        if (sortSetting.equals(favoriteSetting)) {
            selection = sFavoritesSelection;
            selectionArgs = new String[]{"1"};
        } else {
            selection = sSortSettingSelection;
            selectionArgs = new String[]{sortSetting};
        }

        return mDBHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                                                    projection,
                                                    selection,
                                                    selectionArgs,
                                                    null,
                                                    null,
                                                    sortOrder);
    }

    private Cursor getMovie(Uri uri, String[] projection, String selection, String[] selectionArgs,
                            String sortOrder) {
        return mDBHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                                                projection,
                                                selection,
                                                selectionArgs,
                                                null,
                                                null,
                                                sortOrder);
    }

    /*
     * Provider DB functions - Insert, Update, Delete, Query
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                                                    String sortOrder) {
        Cursor retCursor;
        int match = sMatcher.match(uri);

        switch (match) {
            case MOVIE:
                //Log.d(LOG_TAG, "Inside query, movie");
                retCursor = getMovie(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case MOVIE_WITH_ID:
                //Log.d(LOG_TAG, "movie with id");
                retCursor = getMovieWithId(uri, projection, sortOrder);
                break;
            case MOVIE_WITH_SORT_SETTING:
                retCursor = getMovieWithSortSelection(uri, projection, sortOrder);
                //Log.d(LOG_TAG, "movie with sort setting");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    // Insert the values into movie DB and notify the content resolver
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Uri returnUri;
        //Log.d(LOG_TAG, "Inside Insert");

        long rowID = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
        if (rowID > 0)
            returnUri = MovieContract.MovieEntry.buildUriWithId(rowID);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int rowsDel = 0;

        rowsDel = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);

        if (rowsDel != 0 || selection == null)
            getContext().getContentResolver().notifyChange(uri, null);
        Log.d(LOG_TAG, "rows deleted: " + rowsDel);
        return rowsDel;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        int rowsUpdated = 0;

        rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0 || selection == null)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    /*
     * Get the return MIME type of URI
     */
    @Override
    public String getType(Uri uri) {
        final int match = sMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_SORT_SETTING:
                return MovieContract.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /*
     * Build different uri matches possible
     */
    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#",
                MOVIE_WITH_ID);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/*",
                                                                          MOVIE_WITH_SORT_SETTING);

        return matcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mDBHelper.close();
        super.shutdown();
    }
}
