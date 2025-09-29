package io.github.adainish.wynautrankup.tracker;

import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.data.Match;
import io.github.adainish.wynautrankup.util.EloCalculator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Tracks ranked battles using their unique identifiers.
 */
public class RankedBattleTracker
{
    private final HashMap<UUID, Match> rankedBattles = new HashMap<>();

    public void markAsRanked(UUID battleId, Match match) {
        rankedBattles.put(battleId, match);
    }

    public Match getRankedBattle(UUID battleId) {
        return rankedBattles.get(battleId);
    }

    public boolean isRanked(UUID battleId) {
        return rankedBattles.containsKey(battleId);
    }

    public void removeRankedBattle(UUID battleId) {
        rankedBattles.remove(battleId);
    }

    public void clearRankedBattles() {
        rankedBattles.clear();
    }

    public boolean isPlayerInRankedBattle(UUID uuid) {
        return rankedBattles.values().stream().anyMatch(match ->
                match.getPlayer1().getId().equals(uuid) || match.getPlayer2().getId().equals(uuid)
        );
    }

    public void forfeitPlayerFromBattle(UUID uuid) {
        Match match = rankedBattles.values().stream().filter(m ->
                m.getPlayer1().getId().equals(uuid) || m.getPlayer2().getId().equals(uuid)
        ).findFirst().orElse(null);
        if (match != null) {
            match.endMatch();
            rankedBattles.values().remove(match);
            // Inform players about the forfeit
            match.getPlayer1().sendMessage("You forfeited your ranked battle.");
            match.getPlayer2().sendMessage("Your ranked battle has been forfeited.");
            // mark forfeiter as loser and the other player as winner
            UUID winnerId = match.getPlayer1().getId().equals(uuid) ? match.getPlayer2().getId() : match.getPlayer1().getId();

            rankedBattleEnded(match, uuid, winnerId);
        }
    }

    public void rankedBattleEnded(Match match, UUID uuid, UUID winnerId)
    {
        int winnerElo = 0;
        int loserElo = 0;
        try {
            winnerElo = WynautRankUp.instance.playerDataManager.getElo(winnerId).get();
            loserElo = WynautRankUp.instance.playerDataManager.getElo(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (winnerElo == 0 || loserElo == 0) {
            //this should never happen, but just in case
            return;
        }

        System.out.println("Winner ELO: " + winnerElo + " Loser ELO: " + loserElo);
        int eloChange = EloCalculator.calculateEloChange(winnerElo, loserElo, true);
        System.out.println("Elo change: " + eloChange);

        int adjustedWinnerElo = winnerElo + eloChange;
        int adjustedLoserElo = loserElo - eloChange;

        // Ensure ELO does not drop below 1000
        if (adjustedWinnerElo < 1000) adjustedWinnerElo = 1000;
        if (adjustedLoserElo < 1000) adjustedLoserElo = 1000;

        System.out.println("Adjusted Winner ELO: " + adjustedWinnerElo);
        System.out.println("Adjusted Loser ELO: " + adjustedLoserElo);

        //update ELOs in database
        WynautRankUp.instance.playerDataManager.setElo(winnerId, adjustedWinnerElo);
        WynautRankUp.instance.playerDataManager.setElo(uuid, adjustedLoserElo);


        //do appropriate win/loss recording
        WynautRankUp.instance.matchmakingQueue.getMatchResultRecorder().recordMatch(winnerId, uuid, eloChange, -eloChange);
        removeRankedBattle(match.getBattleId());
        //do appropriate win/loss announcement
        match.getPlayer1().sendMessage("Match Result: " + (match.getPlayer1().getId().equals(winnerId) ? "Victory!" : "Defeat."));
        match.getPlayer2().sendMessage("Match Result: " + (match.getPlayer2().getId().equals(winnerId) ? "Victory!" : "Defeat."));

        //TODO: implement rewarding system

        //TODO: Implement tp system to teleport players to designated location if online

        //remove invulnerability and notify players of their new ELO as well as teleport them to a designated location
        if (match.getPlayer1().getOptionalServerPlayer().isPresent()) {
            ServerPlayer serverPlayer = match.getPlayer1().getOptionalServerPlayer().get();
            serverPlayer.setInvulnerable(false);
            if (match.getPlayer1().getId().equals(winnerId)) {
                serverPlayer.sendSystemMessage(Component.literal("You gained " + eloChange + " ELO! New ELO: " + adjustedWinnerElo).withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
            } else {
                serverPlayer.sendMessage(Component.literal("You lost " + eloChange + " ELO. New ELO: " + adjustedLoserElo).withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD));
            }

        }

        if (match.getPlayer2().getOptionalServerPlayer().isPresent()) {
            ServerPlayer serverPlayer = match.getPlayer2().getOptionalServerPlayer().get();
            serverPlayer.setInvulnerable(false);
            if (match.getPlayer2().getId().equals(winnerId)) {
                serverPlayer.sendSystemMessage(Component.literal("You gained " + eloChange + " ELO! New ELO: " + adjustedWinnerElo).withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
            } else {
                serverPlayer.sendMessage(Component.literal("You lost " + eloChange + " ELO. New ELO: " + adjustedLoserElo).withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD));
            }
        }



    }
}

