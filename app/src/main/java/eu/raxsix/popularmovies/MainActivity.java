package eu.raxsix.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import eu.raxsix.popularmovies.fragments.PosterFragment;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG_SORT_POPULAR = "sortPopular";
    private static final String TAG_SORT_RATING = "sortRating";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.posterContainer, new PosterFragment(), "poster")
                    .commit();
        }

        createFloatingActionButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createFloatingActionButton() {

        // Creating icon view for floating action button
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.ic_filter_list_black_48dp);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        // Building subIcons for floating action button
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView popularImage = new ImageView(this);
        popularImage.setImageResource(R.drawable.ic_supervisor_account_black_24dp);
        SubActionButton popular_sort = itemBuilder.setContentView(popularImage).build();
        popular_sort.setTag(TAG_SORT_POPULAR);
        popular_sort.setOnClickListener(this);

        ImageView ratedImage = new ImageView(this);
        ratedImage.setImageResource(R.drawable.ic_stars_black_24dp);
        SubActionButton rated_sort = itemBuilder.setContentView(ratedImage).build();
        rated_sort.setTag(TAG_SORT_RATING);
        rated_sort.setOnClickListener(this);

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .setRadius(200)
                .setStartAngle(180)
                .setEndAngle(230)
                .addSubActionView(popular_sort)
                .addSubActionView(rated_sort)
                .attachTo(actionButton)
                .build();

    }

    @Override
    public void onClick(View v) {

        PosterFragment fragment = (PosterFragment) getSupportFragmentManager().findFragmentByTag("poster");

        if (v.getTag().equals(TAG_SORT_POPULAR)) {

            fragment.onSortByPopular();
            setActionBarTitle("Most popular moives");

        }

        if (v.getTag().equals(TAG_SORT_RATING)) {

            fragment.onSortByRating();
            setActionBarTitle("Highest rated movies");

        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
