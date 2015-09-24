package app.gerardo.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsMovieActivityFragment extends Fragment {

    private static final String MOVIES_SHARE_HASHTAG = "#Popular Movies App";
    private String mMovieStr;
    private String LOG_TAG = "DetailsMovieActivity";

    public DetailsMovieActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();

        View rootView = inflater.inflate(R.layout.fragment_details_movie, container, false);

        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mMovieStr = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
            ((TextView) rootView.findViewById(R.id.details)).setText(mMovieStr);
        }

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
        inflater.inflate(R.menu.details_fragment,menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMoviesIntent());
        }
        else {
            Log.d(LOG_TAG, "Share Action provides is null");
        }

    }
}
