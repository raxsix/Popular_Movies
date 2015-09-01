package eu.raxsix.popularmovies;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.raxsix.popularmovies.adapters.TrailerCursorAdapter;
import eu.raxsix.popularmovies.api_key.ApiKey;
import eu.raxsix.popularmovies.database.MovieContract;
import eu.raxsix.popularmovies.extras.Constants;
import eu.raxsix.popularmovies.network.VolleySingleton;

import static eu.raxsix.popularmovies.extras.Constants.BASE_URL;
import static eu.raxsix.popularmovies.extras.Constants.IMAGE_SIZE;
import static eu.raxsix.popularmovies.extras.Constants.TAG_REQUEST_REVIEW;
import static eu.raxsix.popularmovies.extras.Constants.TAG_REQUEST_TRAILER;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_PREVIEW_CONTENT;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_RESULTS;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_REVIEW_AUTHOR;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_TRAILER_NAME;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_TRAILER_SITE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_TRAILER_SIZE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_TRAILER_TYPE;
import static eu.raxsix.popularmovies.extras.JsonKeys.KEY_YOUTUBE_KEY;

public class MovieDetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mReviewTextView;
    private ImageView mPosterImageView;
    private ImageLoader mImageLoader;
    private VolleySingleton mVolleySingleton;
    private RequestQueue mRequestQueue;
    private TextView mOverview;
    private CheckBox mCheckBox;
    private int mRemoteMovieId;
    private int mLocalMovieId;
    private ListView mListView;
    private ProgressDialog mDialog;
    private Cursor mSetTrailerCursor;

    private JsonObjectRequest mTrailerRequest;
    JsonObjectRequest mReviewRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Log.d(TAG, "onCreate was called");

        mDialog = new ProgressDialog(this);

        // Show the back button in this activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Get Volley to init ImageLoader for cached images
        mVolleySingleton = VolleySingleton.getsInstance();

        // Instantiate the RequestQueue.
        mRequestQueue = mVolleySingleton.getsInstance().getRequestQueue();

        mImageLoader = mVolleySingleton.getImageLoader();

        Intent intent = getIntent();
        Log.d("MOVIE", "---------------------------------------------------------");
        // Extract info from this intent
        mLocalMovieId = intent.getIntExtra(Constants.EXTRA_LOCAL_ID, -1);
        Log.d("MOVIE", mLocalMovieId + "");
        String title = intent.getStringExtra(Constants.EXTRA_TITLE);
        Log.d("MOVIE", title + "");
        String posterImageUrl = intent.getStringExtra(Constants.EXTRA_PATH);
        Log.d("MOVIE", posterImageUrl + "");
        String date = intent.getStringExtra(Constants.EXTRA_DATE);
        Log.d("MOVIE", date + "");
        String overview = intent.getStringExtra(Constants.EXTRA_OVERVIEW);
        Log.d("MOVIE", overview + "");
        Double rating = intent.getDoubleExtra(Constants.EXTRA_RATING, 0);
        Log.d("MOVIE", rating + "");
        int favorite = intent.getIntExtra(Constants.EXTRA_IS_FAVORITE, 0);
        Log.d("MOVIE", favorite + "");
        mRemoteMovieId = intent.getIntExtra(Constants.EXTRA_REMOTE_ID, 0);
        Log.d("MOVIE", mRemoteMovieId + "");

        // Get the references for the widgets
        mTitle = (TextView) findViewById(R.id.titleTextView);
        mPosterImageView = (ImageView) findViewById(R.id.posterImageView);
        mReleaseDate = (TextView) findViewById(R.id.releaseTextView);
        mRating = (TextView) findViewById(R.id.ratingTextView);
        mOverview = (TextView) findViewById(R.id.overviewTextView);
        mReviewTextView = (TextView) findViewById(R.id.reviewTextView);
        mCheckBox = (CheckBox) findViewById(R.id.favoriteCheckBox);
        mListView = (ListView) findViewById(R.id.trailersListView);

        // Set the title
        mTitle.setText(title);

        if (favorite == 0) {
            mCheckBox.setChecked(false);
        } else {
            mCheckBox.setChecked(true);
        }

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int listen = 0;

                if (mCheckBox.isChecked()) {
                    listen = 1;
                }
                ContentValues updateValue = new ContentValues();
                updateValue.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, listen);


                getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        updateValue,
                        MovieContract.MovieEntry.COLUMN_REMOTE_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(mRemoteMovieId)});

            }
        });


        // Set the overview
        mOverview.setText(overview);

        // Set the rating
        mRating.setText(Double.toString(rating) + " / 10");

        // Set the year, take the string and make the Calendar object from it
        try {
            Calendar releaseDate = new GregorianCalendar();
            Date formattedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            releaseDate.setTime(formattedDate);
            String year = Integer.toString(releaseDate.get(Calendar.YEAR));

            // Set the year
            mReleaseDate.setText(year);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        // Build the poster url
        String posterUrl = BASE_URL + IMAGE_SIZE + posterImageUrl;

        // Get the image, it should be cached by Volley, not sure for 100%
        mImageLoader.get(posterUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                // Set the poster
                mPosterImageView.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        getTrailerInfo();

        getReviewInfo();
    }


    private void getTrailerInfo() {
        Log.d(TAG, "getTrailerInfo was called");

        String trailerUrl = Constants.MOVIE_TRAILER_BASE_URL + mRemoteMovieId + "/videos?api_key=" + ApiKey.API_KEY;

        mTrailerRequest = new JsonObjectRequest(Request.Method.GET, trailerUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "trailer onResponse was called");
                // Parse the response
                parseJsonResponse(response, mTrailerRequest.getTag().toString());

                setTrailerInfo();

                mDialog.hide();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mDialog.hide();

            }
        });

        mTrailerRequest.setTag(TAG_REQUEST_TRAILER);
        // Add the request to the RequestQueue.
        mRequestQueue.add(mTrailerRequest);

    }

    private void getReviewInfo() {
        Log.d(TAG, "getReviewInfo was called");

        String reviewUrl = Constants.MOVIE_REVIEW_BASE_URL + mRemoteMovieId + "/reviews?api_key=" + ApiKey.API_KEY;

        mReviewRequest = new JsonObjectRequest(Request.Method.GET, reviewUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Review onResponse was called");
                // Parse the response
                parseJsonResponse(response, mReviewRequest.getTag().toString());

                setReviewInfo();

                mDialog.hide();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mDialog.hide();

            }
        });

        mReviewRequest.setTag(TAG_REQUEST_REVIEW);
        // Add the request to the RequestQueue.
        mRequestQueue.add(mReviewRequest);

    }

    private void setReviewInfo() {

        Log.d(TAG, "setReviewInfo was called");
        StringBuilder sb = new StringBuilder();

        Cursor cursor = getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{String.valueOf(mLocalMovieId)},
                null);

        while (cursor.moveToNext()){

            int authorIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
            Log.d(TAG, "authorIndex: "+ authorIndex);
            int contentIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);
            Log.d(TAG, "contentIndex: " + contentIndex);

            sb.append("\n");
            sb.append(cursor.getString(authorIndex));
            sb.append("\n");
            sb.append(cursor.getString(contentIndex));
            sb.append("\n");
        }

            mReviewTextView.setText(sb);

        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // When press back from action bar then the recyclerview will resume at same position
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void parseJsonResponse(JSONObject response, String tag) {
        Log.d(TAG, "parseJsonResponse was called");
        if (response != null && response.length() > 0) {

            try {

                if (response.has(KEY_RESULTS)) {


                    JSONArray jsonArray = response.getJSONArray(KEY_RESULTS);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        if (tag.equals(TAG_REQUEST_TRAILER)) {

                            // Given a default values for json keys to avoid JSONException when key is null or not there
                            String youtubeKey = Constants.NA;
                            String name = Constants.NA;
                            String site = Constants.NA;
                            int size = -1;
                            String type = Constants.NA;

                            JSONObject movie = jsonArray.getJSONObject(i);


                            if (movie.has(KEY_YOUTUBE_KEY) && !movie.isNull(KEY_YOUTUBE_KEY)) {


                                youtubeKey = movie.getString(KEY_YOUTUBE_KEY);
                            }

                            if (movie.has(KEY_TRAILER_NAME) && !movie.isNull(KEY_TRAILER_NAME)) {


                                name = movie.getString(KEY_TRAILER_NAME);
                            }

                            if (movie.has(KEY_TRAILER_SITE) && !movie.isNull(KEY_TRAILER_SITE)) {

                                site = movie.getString(KEY_TRAILER_SITE);
                            }

                            if (movie.has(KEY_TRAILER_SIZE) && !movie.isNull(KEY_TRAILER_SIZE)) {

                                size = movie.getInt(KEY_TRAILER_SIZE);
                            }

                            if (movie.has(KEY_TRAILER_TYPE) && !movie.isNull(KEY_TRAILER_TYPE)) {

                                type = movie.getString(KEY_TRAILER_TYPE);
                            }


                            // If trailer does not have youtube key do not but it to the db
                            if (!youtubeKey.equals(Constants.NA)) {

                                addTrailerToDatabase(youtubeKey, name, site, size, type);
                            }
                        } else {

                            // Given a default values for json keys to avoid JSONException when key is null or not there

                            String author = Constants.NA;
                            String content = Constants.NA;

                            JSONObject movie = jsonArray.getJSONObject(i);


                            if (movie.has(KEY_REVIEW_AUTHOR) && !movie.isNull(KEY_REVIEW_AUTHOR)) {

                                author = movie.getString(KEY_REVIEW_AUTHOR);
                            }

                            if (movie.has(KEY_PREVIEW_CONTENT) && !movie.isNull(KEY_PREVIEW_CONTENT)) {

                                content = movie.getString(KEY_PREVIEW_CONTENT);
                            }


                            // If trailer does not have youtube key do not but it to the db
                            if (!content.equals(Constants.NA) && !author.equals(Constants.NA)) {

                                addReviewsToDatabase(author, content);
                            }

                        }


                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addReviewsToDatabase(String author, String content) {

        Log.d(TAG, "addReviewsToDatabase was called");

        String[] columns = {MovieContract.ReviewEntry.COLUMN_AUTHOR};

        Cursor reviewCursor = getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                columns,
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " = ?",
                new String[]{String.valueOf(author)},
                null);

        if (!reviewCursor.moveToFirst()) {

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, mLocalMovieId);
            Log.d(TAG, mLocalMovieId + "");
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
            Log.d(TAG, author + "");
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);
            Log.d(TAG, content + "");

            getContentResolver().insert(
                    MovieContract.ReviewEntry.CONTENT_URI,
                    reviewValues);
        }

        reviewCursor.close();
    }

    private void addTrailerToDatabase(String youtubeKey, String name, String site, int size, String type) {

        Log.d(TAG, "addTrailerToDatabase was called");

        String[] columns = {MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY};

        Cursor trailerCursor = getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                columns,
                MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY + " = ?",
                new String[]{String.valueOf(youtubeKey)},
                null);

        if (!trailerCursor.moveToFirst()) {

            ContentValues trailerValues = new ContentValues();

            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, mLocalMovieId);
            Log.d(TAG, mLocalMovieId + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, youtubeKey);
            Log.d(TAG, youtubeKey + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, size);
            Log.d(TAG, size + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, type);
            Log.d(TAG, type + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, name);
            Log.d(TAG, name + "");
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_SITE, site);
            Log.d(TAG, site + "");

            getContentResolver().insert(
                    MovieContract.TrailerEntry.CONTENT_URI,
                    trailerValues);
        }
        trailerCursor.close();
    }

    private void setTrailerInfo() {

        Log.d(TAG, "setTrailerInfo was called");

        mSetTrailerCursor = getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{String.valueOf(mLocalMovieId)},
                null);

        // Setup cursor adapter using cursor from last step
        TrailerCursorAdapter trailerAdapter = new TrailerCursorAdapter(this, mSetTrailerCursor, 0);
        // Attach cursor adapter to the ListView
        mListView.setAdapter(trailerAdapter);

        mListView.setOnItemClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSetTrailerCursor.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d(TAG, "onItemClick was called");

        String[] columns = {MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY};

        Cursor trailerCursor = getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                columns,
                MovieContract.TrailerEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null);

        if (trailerCursor.moveToFirst()) {

            int youtubeKeyIndex = trailerCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);

            Log.d(TAG, "youtubeIndex: " + youtubeKeyIndex);

            String youtubeKey = trailerCursor.getString(youtubeKeyIndex);

            Log.d(TAG, "youtubeKey: " + youtubeKey);

            watchYoutubeVideo(youtubeKey);
        }

        trailerCursor.close();


    }

    private void watchYoutubeVideo(String id) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }
}
