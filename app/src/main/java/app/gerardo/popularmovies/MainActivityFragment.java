package app.gerardo.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import app.gerardo.popularmovies.data.MoviesContract;

/**
 * Fragment with gridview
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    MoviesAdapter adapter;
    private static final int MOVIE_LOADER = 0;
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_RATE = "vote_average.desc";
    private static final String VOTE_COUNT_MIN = "100";
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private GridView mGrid;

    // Specify the columns we need from movie db.
    // We get all columns from movie db
    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_VOTE,
            MoviesContract.MovieEntry.COLUMN_POPULARITY

    };

    // These indices are tied to movie columns.
    static final int COL_WEATHER_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_VOTE = 3;
    static final int COL_MOVIE_POPULARITY = 4;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);

        // We use a Cursor Adapter in order to populate grid view
        adapter = new MoviesAdapter(getActivity(), null, 0);


        // Get reference of gridview
        mGrid = (GridView) rootView.findViewById(R.id.gridview_movies);
        mGrid.setAdapter(adapter);

        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // We get the cursor that we want to display on Details Activity
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailsMovieActivity.class)
                            .setData(MoviesContract.MovieEntry.buildMoviesUri(cursor.getInt(COL_WEATHER_ID))
                            );
                    startActivity(intent);
                }
                mPosition = position;
            }
        });


        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    // Restart data when sort parameter has changed
    void onSortParameterChange(String order, String votes) {
        updateMovies(order, votes);
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
    // Update UI with new data
    private void updateMovies(String order, String voteCount) {
        new FetchMovieTask(getActivity()).execute(order, voteCount);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment, menu);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store state
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Adding action to menu items
        switch (item.getItemId()) {
            case R.id.popularity_radio:
                if (item.isChecked()) {
                    item.setChecked(false);
                }
                else {
                    item.setChecked(true);
                    onSortParameterChange(SORT_BY_POPULARITY, null);
                }
            case R.id.votes_radio:
                if (item.isChecked()) {
                    item.setChecked(false);
                }
                else {
                    item.setChecked(true);
                    onSortParameterChange(SORT_BY_RATE, VOTE_COUNT_MIN);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // Sort order:  Descending, by popularity.
        String sortOrder = MoviesContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        Uri weatherForLocationUri = MoviesContract.MovieEntry.buildMovieUri();

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader
            mGrid.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.swapCursor(null);
    }
}
