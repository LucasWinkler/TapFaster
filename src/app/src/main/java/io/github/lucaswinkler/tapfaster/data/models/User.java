package io.github.lucaswinkler.tapfaster.data.models;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {
    // The users unique properties
    private String id;
    private String username;

    // The users best time which is their lowest average in milliseconds
    private String bestTime;

    public User(String id, String username, String bestTime) {
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

    public String getBestTime() { return bestTime; }

    public String getBestTimeFormatted() { return bestTime + "ms"; }

    public void setBestTime(String bestTime) { this.bestTime = bestTime; }
}
