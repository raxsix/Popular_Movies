package eu.raxsix.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.database.MovieContract;
import eu.raxsix.popularmovies.network.VolleySingleton;

import static eu.raxsix.popularmovies.extras.Constants.BASE_URL;
import static eu.raxsix.popularmovies.extras.Constants.IMAGE_SIZE;

/**
 * Created by Ragnar on 8/30/2015.
 */
public class GridAdapter extends SimpleCursorAdapter {

    public GridAdapter(Context context, int layout) {
        super(context, layout, null, new String[]{MovieContract.MovieEntry.COLUMN_IMAGE_PATH}, new int[]{android.R.id.text1}, 0);
        Log.i("GRID", " in GridAdapter constructor");
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i("GRID", "GridAdapter - in bindView");
        String content = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_PATH));

        String title = content;
        String posterUrl = BASE_URL + IMAGE_SIZE + content;
        Log.d("GRID", posterUrl);

        TextView titleText = (TextView) view.findViewById(android.R.id.text1);
        titleText.setText(title);

        NetworkImageView iconView = (NetworkImageView) view.findViewById(R.id.imageView1);
        iconView.setImageUrl(posterUrl, VolleySingleton.getsInstance().getImageLoader());
    }

}

