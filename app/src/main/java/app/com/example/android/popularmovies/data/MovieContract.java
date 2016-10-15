package app.com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

/**
 * Created by ankit on 8/30/16.
 * Implements Data contract for storing movie details in a database
 */
public class MovieContract {
    // content authority for content provider
    public static final String CONTENT_AUTHORITY = "app.com.example.android.popularmovies";

    // base uri for content provider
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // path for movie database
    public static final String PATH_MOVIE = "movie";

    // base uri for movie database
    public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_MOVIE).build();

    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

    public static final class MovieEntry implements BaseColumns {
        // All the Columns for the movie database
        public static final String TABLE_NAME = "movie";
        // movie id
        public static final String COLUMN_MOVIE_ID = "movie_id";
        // movie title
        public static final String COLUMN_TITLE = "title";
        // sort setting
        public static final String COLUMN_SORT_SETTING = "sort_setting";
        // movie release year
        public static final String COLUMN_YEAR = "release";
        // rating, number as string
        public static final String COLUMN_RATING = "rating";
        // synopsis, text
        public static final String COLUMN_SYNOPSIS = "synopsis";
        // favorite, this will be a boolean value
        public static final String COLUMN_FAVORITE = "favorite";
        // image url
        public static final String COLUMN_IMAGE_URL = "image";

        public static Uri buildUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUriWithSortSetting(String sortSetting) {
            return CONTENT_URI.buildUpon().appendPath(sortSetting).build();
        }

        public static long getRowIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getSortSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
