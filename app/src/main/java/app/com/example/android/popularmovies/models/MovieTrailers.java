package app.com.example.android.popularmovies.models;

import com.google.gson.annotations.SerializedName;

public class MovieTrailers {
    @SerializedName("id")
    private String trailerID;
    @SerializedName("key")
    private String trailerKey;
    @SerializedName("name")
    private String trailerName;

    public String getTrailerID() {
        return trailerID;
    }

    public void setTrailerID(String trailerID) {
        this.trailerID = trailerID;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }
}
