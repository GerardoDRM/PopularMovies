package app.gerardo.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import app.gerardo.popularmovies.data.MoviesContract;

/**
 * Created by Gerardo de la Rosa on 25/09/15.
 */
public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private ArrayAdapter<String> adapter;
    private final Context mContext;

    public FetchMovieTask(Context context, ArrayAdapter<String> forecastAdapter) {
        mContext = context;
        adapter = forecastAdapter;
    }

    private boolean DEBUG = true;

    /* The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }


    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param locationSetting The location string used to request updates from the server.
     * @param cityName A human-readable city name, e.g "Mountain View"
     * @param lat the latitude of the city
     * @param lon the longitude of the city
     * @return the row ID of the added location.
     */
    long addLocation(String locationSetting, String cityName, double lat, double lon) {
        // Students: First, check if the location with this city name exists in the db
        // If it exists, return the current ID
        // Otherwise, insert it using the content resolver and the base URI
        return -1;
    }

    /*
        Students: This code will allow the FetchWeatherTask to continue to return the strings that
        the UX expects so that we can continue to test the application even once we begin using
        the database.
     */
    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        // return strings to keep UI functional for now
        String[] resultStrs = new String[cvv.size()];
        for ( int i = 0; i < cvv.size(); i++ ) {
            ContentValues movieValues = cvv.elementAt(i);
            String title = movieValues.getAsString(MoviesContract.MovieEntry.COLUMN_TITLE);
            resultStrs[i] = title;
        }
        return resultStrs;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getMoviesFromJson(String moviesJsonStr) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";
        final String OWM_MOVIE_ID = "id";
        final String OWM_OVERVIEW = "overview";
        final String OWM_TITLE = "title";
        final String OWM_POPULARITY = "popularity";
        final String OWM_POSTER = "poster_path";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_RELEASE_DATE = "release_date";


        try {
            JSONObject moviesJSON = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJSON.getJSONArray(OWM_LIST);

            // Insert the new movie information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

            for(int i = 0; i < moviesArray.length(); i++) {
                // Get the JSON object representing a movie
                JSONObject movie = moviesArray.getJSONObject(i);

                int movieId = movie.getInt(OWM_MOVIE_ID);
                String title = movie.getString(OWM_TITLE);
                String overview = movie.getString(OWM_OVERVIEW);
                double popularity = movie.getDouble(OWM_POPULARITY);
                double voteAverage = movie.getDouble(OWM_VOTE_AVERAGE);
                String poster = movie.getString(OWM_POSTER);
                String date = movie.getString(OWM_RELEASE_DATE);


                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesContract.MovieEntry._ID, movieId);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_DESCRIPTION, overview);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE, voteAverage);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER, poster);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_DATE, 1419033600L);

                cVVector.add(movieValues);
            }

            // add to database
            if ( cVVector.size() > 0 ) {
                // Student: call bulkInsert to add the weatherEntries to the database here
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);

            }

            // Sort order:  Ascending, by date.
            String sortOrder = MoviesContract.MovieEntry.COLUMN_POPULARITY + " ASC";
            Uri weatherForLocationUri = MoviesContract.MovieEntry.buildMovieUri();

            // Students: Uncomment the next lines to display what what you stored in the bulkInsert

            Cursor cur = mContext.getContentResolver().query(weatherForLocationUri,
                    null, null, null, sortOrder);

            cVVector = new Vector<ContentValues>(cur.getCount());
            if ( cur.moveToFirst() ) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");

            String[] resultStrs = convertContentValuesToUXFormat(cVVector);
            return resultStrs;

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String[] doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String popoularMoviesJSON = null;

        String sort = "popularity.desc";
        String API_KEY = "c77c154b48e61f06b2858716425efb7d";


        try {
            // Construct the URL for the Movie API query
            final String MOVIES_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String QUERY_PARAM = "sort_by";
            final String API_KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, sort)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.v("JSON", "Built URI " + builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            popoularMoviesJSON = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        try {
            return getMoviesFromJson(popoularMoviesJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        if (result != null && adapter != null) {
            adapter.clear();
            List<String> movieList = new ArrayList<String>(Arrays.asList(result));
            adapter.addAll(movieList);
            // New data is back from the server.  Hooray!
        }
    }
}
