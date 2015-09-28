package app.gerardo.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import app.gerardo.popularmovies.data.MoviesContract;

/**
 * Created by Gerardo de la Rosa on 25/09/15.
 */
public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }


    /**
     * Parsing JSON with all movie data from Service and updating UI
     */
    private void getMoviesFromJson(String moviesJsonStr) throws JSONException {

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

                // Get millis from date
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal  = Calendar.getInstance();
                cal.setTime(formatter.parse(date));
                long dateMillis = cal.getTimeInMillis();


                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesContract.MovieEntry._ID, movieId);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_DESCRIPTION, overview);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE, voteAverage);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER, poster);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_DATE, dateMillis);

                cVVector.add(movieValues);
            }

            // add to database
            int inserted = 0;
            if ( cVVector.size() > 0 ) {
                // delete old data so we don't build up an endless history
                mContext.getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI, null, null);

                // Insert movies with bulkInsert
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                mContext.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String[] doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String popoularMoviesJSON = null;

        // These are the params to build the URL
        String sort = params[0];
        String voteCount = params[1];
        String API_KEY = "c77c154b48e61f06b2858716425efb7d";


        try {
            // Build the URL for the Movie API query
            final String MOVIES_BASE_URL =
                    "http://api.themoviedb.org/3/discover/movie?";
            final String QUERY_PARAM_SORT = "sort_by";
            final String API_KEY_PARAM = "api_key";
            // This parameter gives a better filter when getting High rate movies,
            // Because it consider just movies with high vote counts
            final String QUERY_PARAM_VOTES = "vote_count.gte";
            // Build URI
            Uri.Builder builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon();
            builtUri.appendQueryParameter(QUERY_PARAM_SORT, sort);
            builtUri.appendQueryParameter(API_KEY_PARAM, API_KEY);
            // Check if high rate is required
            if(voteCount != null) builtUri.appendQueryParameter(QUERY_PARAM_VOTES, voteCount);
            builtUri.build();

            URL url = new URL(builtUri.toString());
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
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            popoularMoviesJSON = buffer.toString();
            getMoviesFromJson(popoularMoviesJSON);
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // Cannot parse data
        } catch (JSONException e) {
            e.printStackTrace();
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
        return null;
    }
}
