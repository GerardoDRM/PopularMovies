package app.gerardo.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Gerardo de la Rosa on 25/09/15.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    // Ids for uir matcher
    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;
        // Use the addURI function to match each of the types.
        matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/*", MOVIE_WITH_ID);
        // Return the new matcher!
        return matcher;
    }

    //movie_id = ?
    private static final String sMovieId =
            MoviesContract.MovieEntry.TABLE_NAME+
                    "." + MoviesContract.MovieEntry._ID + " = ? ";




    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    /**
     * This method allows to return all data in Movie table and also
     * A single cursor
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        final int match = sUriMatcher.match(uri);
            if(match == MOVIE) {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }
            else if(match == MOVIE_WITH_ID) {
                String movieId = MoviesContract.MovieEntry.getMovieId(uri);
                String[] selectionArgs1;
                String selection1;
                selection1 = sMovieId;
                selectionArgs1 = new String[]{movieId};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection1,
                        selectionArgs1,
                        null,
                        null,
                        sortOrder);
            }
            else{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        if (retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        // content://app.gerardo.popoularmovies/movie
        if(match == MOVIE) return MoviesContract.MovieEntry.CONTENT_TYPE;
        // content://app.gerardo.popoularmovies/movie/1
        else if(match == MOVIE_WITH_ID) return MoviesContract.MovieEntry.CONTENT_TYPE;
        else throw new UnsupportedOperationException("Unknown uri: " + uri);

    }

    /**
     *  This method just insert a single value
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        if(match == MOVIE) {
            long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
            if ( _id > 0 )
                returnUri = MoviesContract.MovieEntry.buildMoviesUri(_id);
            else
                throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     *  This method delete all data on Movie table
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";
        if(match == MOVIE) {
            rowsDeleted = db.delete(
                    MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
        }
        else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify listener
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return rows deleted
        return rowsDeleted;
    }

    /**
     * If data on database needs to be updated
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        if(match == MOVIE) {
            rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection,
                    selectionArgs);
        }
        else {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     *  BulkInsert is better when we try to
     *  insert too much data
     * @param uri
     * @param values
     * @return
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        if(match == MOVIE) {
            db.beginTransaction();
            int returnCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return returnCount;
        }
        else {
            return super.bulkInsert(uri, values);
        }
    }

    // Method to assist testing
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
