package eu.raxsix.popularmovies.extras;

/**
 * Created by Ragnar on 8/23/2015.
 */
public interface Constants {

    String BASE_REQUEST_URL = "http://api.themoviedb.org/3/discover/movie?";
    String POPULAR_MOVIES = "sort_by=popularity.desc&api_key=";
    String KIDS_MOVIES = "certification_country=US&certification.lte=G&sort_by=popularity.desc&api_key=";

    String NA = "NA";
    String BASE_URL = "http://image.tmdb.org/t/p/";
    String IMAGE_SIZE = "w500";

    String TAG_SORT_POPULAR = "sortPopular";
    String TAG_SORT_RATING = "sortRating";
    String TAG_SORT_FAVORITES = "sortFavorite";

    String TAG_REQUEST_POPULAR = "popular";
    String TAG_REQUEST_RATED = "rated";

    String EXTRA_TITLE = "TITLE";
    String EXTRA_PATH = "PATH";
    String EXTRA_OVERVIEW = "OVERVIEW";
    String EXTRA_RATING = "RATING";
    String EXTRA_DATE = "DATE";
    String EXTRA_IS_FAVORITE = "FAVORITE";
    String EXTRA_ID = "ID";

}
