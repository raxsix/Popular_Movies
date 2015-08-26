package eu.raxsix.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import eu.raxsix.popularmovies.extras.Constants;
import eu.raxsix.popularmovies.network.VolleySingleton;

import static eu.raxsix.popularmovies.extras.Constants.BASE_URL;
import static eu.raxsix.popularmovies.extras.Constants.IMAGE_SIZE;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mRating;
    private ImageView mPosterImageView;
    private ImageLoader mImageLoader;
    private VolleySingleton mVolleySingleton;
    private TextView mOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Show the back button in this activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get Volley to init ImageLoader for cached images
        mVolleySingleton = VolleySingleton.getsInstance();
        mImageLoader = mVolleySingleton.getImageLoader();

        Intent intent = getIntent();

        // Extract info from this intent
        String title = intent.getStringExtra(Constants.EXTRA_TITLE);
        String posterImageUrl = intent.getStringExtra(Constants.EXTRA_PATH);
        String date = intent.getStringExtra(Constants.EXTRA_DATE);
        String overview = intent.getStringExtra(Constants.EXTRA_OVERVIEW);
        Double rating = intent.getDoubleExtra(Constants.EXTRA_RATING, 0);


        // Get the references for the widgets
        mTitle = (TextView) findViewById(R.id.titleTextView);
        mPosterImageView = (ImageView) findViewById(R.id.posterImageView);
        mReleaseDate = (TextView) findViewById(R.id.releaseTextView);
        mRating = (TextView) findViewById(R.id.ratingTextView);
        mOverview = (TextView) findViewById(R.id.overviewTextView);

        // Set the title
        mTitle.setText(title);

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
}
