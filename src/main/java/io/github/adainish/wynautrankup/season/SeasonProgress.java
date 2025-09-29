package io.github.adainish.wynautrankup.season;

public class SeasonProgress
{
    private final String season;
    private final int winStreak;

    public SeasonProgress(String season, int winStreak) {
        this.season = season;
        this.winStreak = winStreak;
    }

    public String getSeason() {
        return season;
    }

    public int getWinStreak() {
        return winStreak;
    }
}
