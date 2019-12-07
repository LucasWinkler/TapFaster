package io.github.lucaswinkler.tapfaster.data.models;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {
    // The users unique properties
    private String id;
    private String username;

    // The users best time which is their lowest average in milliseconds
    private int bestTime;

    public User(String id, String username, int bestTime) {
        this.id = id;
        this.username = username;
        this.bestTime = bestTime;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getBestTime() { return bestTime; }

    public String getBestTimeToString() { return bestTime + " ms"; }

    public void setBestTime(int bestTime) { this.bestTime = bestTime; }
}
