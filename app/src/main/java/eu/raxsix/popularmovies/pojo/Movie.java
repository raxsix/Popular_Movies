package eu.raxsix.popularmovies.pojo;

/**
 * Created by Ragnar on 8/23/2015.
 */
public class Movie {

    private long id;
    private String title;
    private String posterImagePath;
    private String overview;
    private double rating;
    private String releaseDate;
    private boolean isFavorite;


    public Movie(long id, String title, String posterImagePath, String overview, double rating, String releaseDate, boolean isFavorite) {

        this.id = id;
        this.title = title;
        this.posterImagePath = posterImagePath;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.isFavorite = isFavorite;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterImagePath(String posterImagePath) {
        this.posterImagePath = posterImagePath;
    }

    public String getPosterImagePath() {
        return posterImagePath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean getisFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
