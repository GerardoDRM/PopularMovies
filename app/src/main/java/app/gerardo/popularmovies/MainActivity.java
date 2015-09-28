package app.gerardo.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String MAIN_FRAGMENT = "MOVIE_FRAGMENT";

    private static final String SORT_BY_POPULARITY = "popularity.desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new MainActivityFragment(), MAIN_FRAGMENT)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivityFragment movieF = (MainActivityFragment)getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
        if ( null != movieF ) {
            movieF.onSortParameterChange(SORT_BY_POPULARITY, null);
        }
    }
}
