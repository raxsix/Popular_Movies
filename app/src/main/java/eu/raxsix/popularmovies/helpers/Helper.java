package eu.raxsix.popularmovies.helpers;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by Ragnar on 9/2/2015.
 */
public class Helper {

    private static final String TAG = Helper.class.getSimpleName();

    public static void getCursorInfo(Cursor cursor){

        Log.d(TAG, "getCursorInfo method was called");

        Log.d(TAG, "row count: " + cursor.getCount());

        Log.d(TAG, "column count: " + cursor.getColumnCount());

        String[] columnNames = cursor.getColumnNames();

        for (int i = 0; i <cursor.getColumnCount() ; i++) {

            Log.d(TAG, i+ " column name: " + columnNames[i]);
        }
    }
}
