package app.gerardo.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Gerardo de la Rosa on 23/09/15.
 */
public class MoviesContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "app.gerardo.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_MOVIE = "movie";


    /* This Inner class defines the table content of Movie Table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        // Column with the movie title
        public static final String COLUMN_TITLE = "title";
        // Column with movie description
        public static final String COLUMN_DESCRIPTION = "overview";
        // Release date stored as long in milliseconds.
        public static final String COLUMN_DATE = "release_date";
        // Column with poster path
        public static final String COLUMN_POSTER = "poster_path";
        // Column with popularity value (float)
        public static final String COLUMN_POPULARITY = "popularity";
        // Movie vote average (float)
        public static final String COLUMN_VOTE = "vote_average";

        public static Uri buildMovieUri() {
            return CONTENT_URI;
        }

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
