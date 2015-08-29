package eu.raxsix.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import eu.raxsix.popularmovies.database.MovieContract;
import eu.raxsix.popularmovies.database.MovieDbHelper;

/**
 * Created by Ragnar on 8/27/2015.
 * Default movie values for your database tests.
 */
public class TestUtilities extends AndroidTestCase {


    static ContentValues createMovieValues() {

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_REMOTE_MOVIE_ID, 76341);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Spider Man");
        movieValues.put(MovieContract.MovieEntry.COLUMN_DATE, "2015-10-12");
        movieValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_PATH, "adsfasdgasga");
        movieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, 1);
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Long text about the movie");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 6.8);

        return movieValues;
    }


    static ContentValues createTrailerValues(long movieRowId) {

        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movieRowId);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, "lP-sUUUfamw");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "Official Trailer 3");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_SITE, "YouTube");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, 720);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, "Trailer");

        return trailerValues;
    }

    static ContentValues createReviewValues(long movieRowId) {

        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "Ganesan");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "Overall action packed movie... But there should be more puzzles in the climax... But I really love the movie.... Excellent...");


        return reviewValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

}
