package app.gerardo.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import app.gerardo.popularmovies.Utils.PaletteTransformation;
import app.gerardo.popularmovies.data.MoviesContract;

/**
 * Fragment view for Movie Details
 */
public class DetailsMovieActivityFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String MOVIES_SHARE_HASHTAG = "#Popular Movies App";
    private String mMovieStr;
    private String LOG_TAG = "DetailsMovieActivity";

    private static final int DETAIL_LOADER = 0;
    // Fetch and store ShareActionProvider
    ShareActionProvider mShareActionProvider;

    CollapsingToolbarLayout collapsingToolbar;
    ImageView header;
    TextView mPopularityText, mVotesText, mDescriptionText, mDateText;


    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_VOTE,
            MoviesContract.MovieEntry.COLUMN_POPULARITY,
            MoviesContract.MovieEntry.COLUMN_DATE,
            MoviesContract.MovieEntry.COLUMN_DESCRIPTION



    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_POSTER = 2;
    static final int COL_MOVIE_VOTE = 3;
    static final int COL_MOVIE_POPULARITY = 4;
    static final int COL_MOVIE_DATE = 5;
    static final int COL_MOVIE_DESCRIPTION = 6;


    public DetailsMovieActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details_movie, container, false);

        collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);

        header = (ImageView) rootView.findViewById(R.id.header_img_poster);
        mPopularityText = (TextView) rootView.findViewById(R.id.popularity_data);
        mVotesText = (TextView) rootView.findViewById(R.id.votes_data);
        mDescriptionText = (TextView) rootView.findViewById(R.id.overview_data);
        mDateText = (TextView) rootView.findViewById(R.id.release_date_data);

        return rootView;
    }

    private Intent createShareMoviesIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // This Flag is deprecated but I still use it because the app
        // has min SDK 15
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mMovieStr + MOVIES_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_fragment, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mMovieStr != null) {
            mShareActionProvider.setShareIntent(createShareMoviesIntent());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        Log.d("URI", intent.getData().toString());

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (data != null && data.moveToFirst()) {

        mMovieStr = data.getString(COL_MOVIE_TITLE);
            String mImagePoster = data.getString(COL_MOVIE_POSTER);
        String mPopularity = String.valueOf(data.getDouble(COL_MOVIE_POPULARITY));
            String mVotes = String.valueOf(data.getDouble(COL_MOVIE_VOTE));
            String mOverView = data.getString(COL_MOVIE_DESCRIPTION);
        long date = data.getLong(COL_MOVIE_DATE);

        // Get string from date
        DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        // Adding title name
        collapsingToolbar.setTitle(mMovieStr);
        // Adding image with Picasso and changing color to status bar and collapse
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500//" + mImagePoster).transform(new PaletteTransformation(collapsingToolbar)).into(header);
        // Adding popularity
        mPopularityText.setText(mPopularity);
        // Adding votes
        mVotesText.setText(mVotes);
        // Adding Overview
        mDescriptionText.setText(mOverView);
        // Adding release date
        mDateText.setText(formatter.format(calendar.getTime()));

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMoviesIntent());
        }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
