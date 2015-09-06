package eu.raxsix.popularmovies.extras;

/**
 * Created by Ragnar on 8/23/2015.
 */
public interface Constants {

    String BASE_REQUEST_URL = "http://api.themoviedb.org/3/discover/movie?";
    String POPULAR_MOVIES = "sort_by=popularity.desc&api_key=";
    String KIDS_MOVIES = "certification_country=US&certification.lte=G&sort_by=popularity.desc&api_key=";
    String MOVIE_TRAILER_BASE_URL = "http://api.themoviedb.org/3/movie/";
    String MOVIE_REVIEW_BASE_URL = "http://api.themoviedb.org/3/movie/";


    String NA = "NA";
    String BASE_URL = "http://image.tmdb.org/t/p/";
    String IMAGE_SIZE = "w500";

    String TAG_SORT_POPULAR = "sortPopular";
    String TAG_SORT_RATING = "sortRating";
    String TAG_SORT_FAVORITES = "sortFavorite";

    String TAG_REQUEST_POPULAR = "popular";
    String TAG_REQUEST_RATED = "rated";

    String TAG_REQUEST_TRAILER = "trailer";
    String TAG_REQUEST_REVIEW = "review";

    String EXTRA_TITLE = "title";
    String EXTRA_PATH = "path";
    String EXTRA_OVERVIEW = "overview";
    String EXTRA_RATING = "rating";
    String EXTRA_DATE = "date";
    String EXTRA_IS_FAVORITE = "favorite";
    String EXTRA_REMOTE_ID = "remoteId";
    String EXTRA_LOCAL_ID = "localId";

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_REMOTE_MOVIE_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_IMAGE_PATH = 3;
    public static final int COL_DATE = 4;

    public static final int COL_RATING = 6;
    public static final int COL_FAVORITE = 7;
    public static final int COL_OVERVIEW = 9;

}
