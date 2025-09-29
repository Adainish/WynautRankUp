package io.github.adainish.wynautrankup.ranks;

public class Rank {
    private String name;
    private String displayName;
    private int minElo;

    public Rank(String name, String displayName, int minElo) {
        this.name = name;
        this.displayName = displayName;
        this.minElo = minElo;
    }

    public String getName() { return name; }
    public int getMinElo() { return minElo; }
    public String getDisplayName() { return displayName; }
}
