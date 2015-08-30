package eu.raxsix.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import eu.raxsix.popularmovies.Interfaces.OnFragmentInteractionListener;
import eu.raxsix.popularmovies.extras.Constants;
import eu.raxsix.popularmovies.fragments.ItemGridFragment;
import eu.raxsix.popularmovies.fragments.PosterFragment;

/**
 * NB! First thing you have to do is but your movies API key to
 * @see eu.raxsix.popularmovies.api_key.ApiKey
 *
 * Using Volley instead Piccasso library
 *
 * The highest rated movie url does not work for me I used Kids movies for alternative
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnFragmentInteractionListener {

    private Fragment mGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {

            mGridFragment = new ItemGridFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.posterContainer, mGridFragment, "poster")
                    .commit();
            Log.d("GRID", "Activity - onCreate -ItemGridFragment is added");
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
        popularImage.setImageResource(R.drawable.ic_stars_black_24dp);
        SubActionButton popular_sort = itemBuilder.setContentView(popularImage).build();
        // Setting tag for this button to help manage click event
        popular_sort.setTag(Constants.TAG_SORT_POPULAR);
        popular_sort.setOnClickListener(this);

        // Building up kids movies button
        ImageView ratedImage = new ImageView(this);
        ratedImage.setImageResource(R.drawable.ic_supervisor_account_black_24dp);
        SubActionButton rated_sort = itemBuilder.setContentView(ratedImage).build();
        // Setting tag for this button to help manage click event
        rated_sort.setTag(Constants.TAG_SORT_RATING);
        rated_sort.setOnClickListener(this);

        // Putting the menu together
        new FloatingActionMenu.Builder(this)
                .setRadius(200)
                .setStartAngle(180)
                .setEndAngle(230)
                .addSubActionView(popular_sort)
                .addSubActionView(rated_sort)
                .attachTo(actionButton)
                .build();

    }

    /**
     * Handling floating button click events
     */
    @Override
    public void onClick(View v) {

        // Getting the Poster fragment reference
        ItemGridFragment fragment = (ItemGridFragment) getSupportFragmentManager().findFragmentByTag("poster");


        if (v.getTag().equals(Constants.TAG_SORT_POPULAR)) {

            // PosterFragment implements custom interface SortListener so we get access to the onSortByPopular();
            fragment.onSortByPopular();

            // Set the action bar title to popular movies
            setActionBarTitle(getString(R.string.popular_movies_title));

        }

        if (v.getTag().equals(Constants.TAG_SORT_RATING)) {

            fragment.onSortByRating();
            setActionBarTitle(getString(R.string.kids_movies_title));

        }
    }

    /**
     * Custom method to set the action bar title
     */
    private void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onItemSelected(long id) {

    }
}
