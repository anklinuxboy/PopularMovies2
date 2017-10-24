package app.com.example.android.popularmovies.models;

import com.google.gson.annotations.SerializedName;

public class MovieReviews {
    @SerializedName("author")
    private String reviewAuthor;
    @SerializedName("content")
    private String review;

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReview() {
        return review;
    }
}
