package eu.raxsix.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import eu.raxsix.popularmovies.network.VolleySingleton;

import static eu.raxsix.popularmovies.extras.Constants.BASE_URL;
import static eu.raxsix.popularmovies.extras.Constants.IMAGE_SIZE;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageLoader mImageLoader;
    private TextView mTitle;
    private ImageView mPosterImageView;
    private VolleySingleton mVolleySingleton;
    private TextView mReleaseDate;
    private TextView mRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVolleySingleton = VolleySingleton.getsInstance();
        mImageLoader = mVolleySingleton.getImageLoader();

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String posterImageUrl = intent.getStringExtra("path");
        String date = intent.getStringExtra("date");
        Double rating = intent.getDoubleExtra("rating", 0);

        Log.d("rating", rating + "");

        Log.d("date", date);

        mTitle = (TextView) findViewById(R.id.titleTextView);
        mPosterImageView = (ImageView) findViewById(R.id.posterImageView);
        mReleaseDate = (TextView) findViewById(R.id.releaseTextView);
        mRating = (TextView) findViewById(R.id.ratingTextView);

        mTitle.setText(title);
        mRating.setText(Double.toString(rating) + " / 10");

        try {
            Calendar releaseDate = new GregorianCalendar();
            Date formattedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            releaseDate.setTime(formattedDate);
            String year = Integer.toString(releaseDate.get(Calendar.YEAR));
            mReleaseDate.setText(year);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String url = BASE_URL + IMAGE_SIZE + posterImageUrl;

        mImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

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

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
