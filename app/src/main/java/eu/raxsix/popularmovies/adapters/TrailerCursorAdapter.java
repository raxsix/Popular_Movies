package eu.raxsix.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import eu.raxsix.popularmovies.R;
import eu.raxsix.popularmovies.database.MovieContract;

/**
 * Created by Ragnar on 8/31/2015.
 */
public class TrailerCursorAdapter extends CursorAdapter {

    private static final String TAG = TrailerCursorAdapter.class.getSimpleName();

    Context mContext;

    public TrailerCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        Log.d(TAG, "TrailerCursorAdapter - constructor was called");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.d(TAG, "TrailerCursorAdapter - newView was called");
        return LayoutInflater.from(mContext).inflate(R.layout.item_trailer, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG, "TrailerCursorAdapter - bindView was called");
        TextView trailerName = (TextView) view.findViewById(R.id.trailerListTextView);

        String data = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.TrailerEntry.COLUMN_NAME));

        trailerName.setText(data);
    }
}
