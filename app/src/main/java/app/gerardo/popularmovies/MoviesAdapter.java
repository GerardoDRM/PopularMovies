package app.gerardo.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Gerardo de la Rosa on 27/09/15.
 */
public class MoviesAdapter extends CursorAdapter {
    private Context mContext;
    public MoviesAdapter(Context context, Cursor c, int i) {
        super(context, c);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movies, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Get data from db
        String title = cursor.getString(MainActivityFragment.COL_MOVIE_TITLE);
        String poster = cursor.getString(MainActivityFragment.COL_MOVIE_POSTER);

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // Adding movie title
        viewHolder.movieTitle.setText(title);
        // Get image with Picasso
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185//" + poster).into(viewHolder.moviePoster);
    }

    /**
     * Cache of the children views for a grid movie item.
     */
    public static class ViewHolder {
        public final TextView movieTitle;
        public final ImageView moviePoster;

        public ViewHolder(View view) {
            movieTitle = (TextView)view.findViewById(R.id.grid_item_title);
            moviePoster = (ImageView) view.findViewById(R.id.grid_item_image_poster);
        }
    }

}
