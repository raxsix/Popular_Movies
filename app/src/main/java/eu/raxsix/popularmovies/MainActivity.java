package eu.raxsix.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import eu.raxsix.popularmovies.Interfaces.OnFragmentInteractionListener;
import eu.raxsix.popularmovies.database.MovieContract;
import eu.raxsix.popularmovies.extras.Constants;
import eu.raxsix.popularmovies.fragments.MovieDetailFragment;
import eu.raxsix.popularmovies.fragments.PosterGridFragment;

/**
 * NB! First thing you have to do is but your movies API key to
 *
 * @see eu.raxsix.popularmovies.api_key.ApiKey
 * <p/>
 * Using Volley instead Piccasso library
 * <p/>
 * The highest rated movie url does not work for me I used Kids movies for alternative
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnFragmentInteractionListener {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";


    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {

                setupFragment(MovieContract.MovieEntry.buildMovieUri(1));
            }
        } else {
            mTwoPane = false;
        }

        // Call to build up the floating action button
        createFloatingActionButton();
    }

    /**
     * Custom method for creating the floating action button for sorting
     * Using CircularFloatingActionMenu framework
     */
    private void createFloatingActionButton() {

        // Creating icon view for floating action button
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.ic_filter_list_black_48dp);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        // Building subIcons for floating action button
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        // Building up popular movies list button
        ImageView popularImage = new ImageView(this);
        popularImage.setImageResource(R.drawable.ic_supervisor_account_black_24dp);
        SubActionButton popular_sort = itemBuilder.setContentView(popularImage).build();
        // Setting tag for this button to help manage click event
        popular_sort.setTag(Constants.TAG_SORT_POPULAR);
        popular_sort.setOnClickListener(this);

        // Building up kids movies button
        ImageView ratedImage = new ImageView(this);
        ratedImage.setImageResource(R.drawable.ic_star_half_black_48dp);
        SubActionButton rated_sort = itemBuilder.setContentView(ratedImage).build();
        // Setting tag for this button to help manage click event
        rated_sort.setTag(Constants.TAG_SORT_RATING);
        rated_sort.setOnClickListener(this);

        // Building up kids movies button
        ImageView favoriteImage = new ImageView(this);
        favoriteImage.setImageResource(R.drawable.ic_stars_black_24dp);
        SubActionButton favorite_sort = itemBuilder.setContentView(favoriteImage).build();
        // Setting tag for this button to help manage click event
        favorite_sort.setTag(Constants.TAG_SORT_FAVORITES);
        favorite_sort.setOnClickListener(this);

        // Putting the menu together
        new FloatingActionMenu.Builder(this)
                .setRadius(200)
                .setStartAngle(180)
                .setEndAngle(280)
                .addSubActionView(popular_sort)
                .addSubActionView(rated_sort)
                .addSubActionView(favorite_sort)
                .attachTo(actionButton)
                .build();

    }

    /**
     * Handling floating button click events
     */
    @Override
    public void onClick(View v) {

        // Getting the Poster fragment reference
        PosterGridFragment fragment = (PosterGridFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_poster);


        if (v.getTag().equals(Constants.TAG_SORT_POPULAR)) {

            // PosterFragment implements custom interface SortListener so we get access to the onSortByPopular();
            fragment.onSortByPopular();

            // Set the action bar title to popular movies
            setActionBarTitle(getString(R.string.popular_movies_title));

        }

        if (v.getTag().equals(Constants.TAG_SORT_RATING)) {

            fragment.onSortByRating();
            setActionBarTitle(getString(R.string.most_rated_title));

        }

        if (v.getTag().equals(Constants.TAG_SORT_FAVORITES)) {

            fragment.onSortByFavorites();
            setActionBarTitle(getString(R.string.favorites_title));

        }
    }

    /**
     * Custom method to set the action bar title
     */
    private void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public void onItemSelected(Uri contentUri) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            setupFragment(contentUri);

        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }

    private void setupFragment(Uri uri) {

        Bundle args = new Bundle();
        args.putParcelable(MovieDetailFragment.DETAIL_URI, uri);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                .commit();

    }
}
