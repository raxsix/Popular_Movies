package eu.raxsix.popularmovies;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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


    private Fragment mPosterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("LC", "Activity onCreate");

        Log.d("test", "MainActivity onCreate");


        if (savedInstanceState == null) {
            Log.d("test", "saveinstanceState is null");
            mPosterFragment = new PosterFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.posterContainer, mPosterFragment, "poster")
                    .commit();
        } else {
            Log.d("test", "retain");
            mPosterFragment = getSupportFragmentManager().findFragmentByTag("poster");
        }

        createFloatingActionButton();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("LC", "Activity onStop");

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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("LC", "Activity onDestroy");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.i("LC", "Activity onConfigurationChanged");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("LC", "Activity onBackPressed");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("LC", "Activity onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("LC", "Activity onResume");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        Log.i("LC", "Activity onResumeFragments");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("LC", "Activity onSaveInstanceState");

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i("LC", "Activity onStart");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        Log.i("LC", "Activity onAttachFragment");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i("LC", "Activity onRestart");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i("LC", "Activity onRestoreInstanceState");
    }
}
