package eu.raxsix.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.database.MovieContract;
import eu.raxsix.popularmovies.extras.Constants;
import eu.raxsix.popularmovies.network.VolleySingleton;
import eu.raxsix.popularmovies.pojo.Movie;

import static eu.raxsix.popularmovies.extras.Constants.BASE_URL;
import static eu.raxsix.popularmovies.extras.Constants.IMAGE_SIZE;

/**
 * Created by Ragnar on 8/30/2015.
 */
public class MovieGridAdapter extends CursorAdapter {

    private VolleySingleton mVolleySingleton;
    private ImageLoader mImageLoader;
    private List<Movie> movies;

    public MovieGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mVolleySingleton = VolleySingleton.getsInstance();
        mImageLoader = mVolleySingleton.getImageLoader();
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tv = (TextView) view;
        tv.setText(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));

    }

}
