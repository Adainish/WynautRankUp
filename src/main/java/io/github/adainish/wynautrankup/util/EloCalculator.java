package io.github.adainish.wynautrankup.util;

/**
 * Utility class for calculating Elo rating changes.
 */
public class EloCalculator
{
    private static final int K = 32; // K-factor for Elo calculation

    public static int calculateEloChange(int playerElo, int opponentElo, boolean isWin) {
        double expectedScore = 1.0 / (1.0 + Math.pow(10, (opponentElo - playerElo) / 400.0));
        double actualScore = isWin ? 1.0 : 0.0;
        return (int) Math.round(K * (actualScore - expectedScore));
    }
}
