package app.gerardo.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import app.gerardo.popularmovies.data.MoviesContract.MovieEntry;
/**
 * Created by Gerardo de la Rosa on 23/09/15.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    // Database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL query in order to create our movie table
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                MovieEntry.COLUMN_DATE + " INTEGER NOT NULL," +
                MovieEntry.COLUMN_POSTER + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                MovieEntry.COLUMN_VOTE + " REAL NOT NULL)";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We just want this database to cache the online data, so when
        // the database is upgraded the data will be discarded
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
