/*
 * Program: WynautRankup - Add a competitive ranked system to Cobblemon
 * Copyright (C) <2025> <Nicole "Adenydd" Catherine Stuut>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * See the `LICENSE` file in the project root or <https://www.gnu.org/licenses/>.
 */
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
