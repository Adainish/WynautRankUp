package io.github.adainish.wynautrankup.data;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.arenas.Arena;
import io.github.adainish.wynautrankup.database.MatchResultRecorder;
import io.github.adainish.wynautrankup.tracker.RankedBattleTracker;
import io.github.adainish.wynautrankup.util.BattleUtil;
import io.github.adainish.wynautrankup.util.PermissionUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Comparator;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Manages the matchmaking queue for players.
 * Players are matched based on their ELO ratings.
 * Uses a priority queue to efficiently find matches.
 * Players can join or leave the queue, and matches are started automatically.
 * Matches are marked as ranked battles.
 * Thread-safe operations are ensured using synchronization.
 * Processes the queue at regular intervals using a scheduled executor service.
 * Handles player join and leave events.
 * Integrates with PlayerDataManager to fetch player ELO ratings.
 * Integrates with RankedBattleTracker to track ranked battles.
 * Integrates with MatchResultRecorder to record match results.
 */
public class MatchMakingQueue
{
    private final Map<UUID, PlayerQueueEntry> queue = new HashMap<>();
    private final RankedBattleTracker rankedBattleTracker = new RankedBattleTracker();
    private final MatchResultRecorder matchResultRecorder = new MatchResultRecorder();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public MatchMakingQueue() {
        startQueueProcessing();
    }


    /**
     * Handles player joining the matchmaking queue.
     * @param playerId The UUID of the player joining the queue.
     */
    public void onPlayerJoinQueue(UUID playerId, List<Pokemon> pokemonTeam) {
        addToQueue(playerId, pokemonTeam);
        //send message to player
        System.out.println("Player " + playerId + " joined the queue.");
    }

    /**
     * Handles player leaving the matchmaking queue.
     * @param playerId The UUID of the player leaving the queue.
     */
    public void onPlayerLeaveQueue(UUID playerId) {
        synchronized (queue) {
            queue.remove(playerId);
        }
        //send message to player
        PermissionUtil.getOptionalServerPlayer(playerId).ifPresent(player -> {
            player.sendSystemMessage(Component.literal("You have left the ranked queue.").withStyle(s -> s.withColor(0xFF5555)));
        });
    }

    /**
     * Starts the queue processing executor service.
     * Periodically checks for matches and starts them.
     */
    public void startQueueProcessing() {
        executor.scheduleAtFixedRate(() -> {
            synchronized (queue) {
                Optional<Match> match = findMatch();
                match.ifPresent(this::startMatch);
            }
        }, 0, 1, TimeUnit.SECONDS); // Adjust interval as needed
    }

    /**
     * Stops the queue processing executor service.
     * Ensures all ongoing tasks are completed before shutdown.
     */
    public void stopQueueProcessing() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }



    private void startMatch(Match match) {
        // Start the match and mark it as ranked
        Arena arena = match.getArena();
        if (arena == null) {
            // Should not happen, but safety check
            return;
        }
        arena.setInUse(true);

        PlayerQueueEntry entry1 = queue.get(match.getPlayer1().getId());
        PlayerQueueEntry entry2 = queue.get(match.getPlayer2().getId());

        if (entry1 == null || entry2 == null) {
            // One of the players is no longer in the queue, do not start the match
            System.out.println("One of the players is no longer in the queue, match not started.");
            return;
        }

        //check whether both players are still online
        if (PermissionUtil.getOptionalServerPlayer(match.getPlayer1().getId()).isEmpty() ||
                PermissionUtil.getOptionalServerPlayer(match.getPlayer2().getId()).isEmpty()) {
            //one or both players are offline, do not start the match
            //notify the online player if applicable
            PermissionUtil.getOptionalServerPlayer(match.getPlayer1().getId()).ifPresent(player -> {
                player.sendSystemMessage(Component.literal("Your opponent has gone offline. Match cancelled.").withStyle(s -> s.withColor(0xFF5555)));
            });
            PermissionUtil.getOptionalServerPlayer(match.getPlayer2().getId()).ifPresent(player -> {
                player.sendSystemMessage(Component.literal("Your opponent has gone offline. Match cancelled.").withStyle(s -> s.withColor(0xFF5555)));
            });
            return;
        }

        List<Pokemon> team1 = entry1.getTeam();
        List<Pokemon> team2 = entry2.getTeam();

        // Validate both teams
        boolean team1Legal = WynautRankUp.instance.teamValidator.isTeamLegal(team1);
        boolean team2Legal = WynautRankUp.instance.teamValidator.isTeamLegal(team2);
        //verify if the copied teams still match up with the players current teams
        if (!WynautRankUp.instance.teamValidator.doTeamsMatch(entry1.getPlayer(), team1)) {
            PermissionUtil.getOptionalServerPlayer(entry1.getPlayer().getId()).ifPresent(player -> {
                player.sendSystemMessage(Component.literal("Your team has changed since you entered the queue. Please rejoin the queue with your new team.").withStyle(s -> s.withColor(0xFF5555)));
                WynautRankUp.instance.teamValidator.getTeamMismatchReasons(entry1.getPlayer(), team1).forEach(reason -> {
                    player.sendSystemMessage(Component.literal("- " + reason).withStyle(s -> s.withColor(0xFF5555)));
                });
                player.sendSystemMessage(Component.literal("Please rejoin the queue with a legal team and do not change your team while in queue.").withStyle(s -> s.withColor(0xFF5555)));
                queue.remove(entry1.getPlayer().getId());
                entry2.getPlayer().getOptionalServerPlayer().ifPresent(p ->
                        p.sendSystemMessage(Component.literal("Your opponent has changed their team since entering the queue. You have been automatically requeued.").withStyle(s -> s.withColor(0xFF5555)))
                );
                queue.remove(entry2.getPlayer().getId());
                addToQueue(entry2.player.getId(), team1);
            });
            return;
        }
        if (!WynautRankUp.instance.teamValidator.doTeamsMatch(entry2.getPlayer(), team2)) {
            PermissionUtil.getOptionalServerPlayer(entry2.getPlayer().getId()).ifPresent(player -> {
                player.sendSystemMessage(Component.literal("Your team has changed since you entered the queue. Please rejoin the queue with your new team.").withStyle(s -> s.withColor(0xFF5555)));
                WynautRankUp.instance.teamValidator.getTeamMismatchReasons(entry2.getPlayer(), team2).forEach(reason -> {
                    player.sendSystemMessage(Component.literal("- " + reason).withStyle(s -> s.withColor(0xFF5555)));
                });
                player.sendSystemMessage(Component.literal("Please rejoin the queue with a legal team and do not change your team while in queue.").withStyle(s -> s.withColor(0xFF5555)));
                queue.remove(entry2.getPlayer().getId());
                entry1.getPlayer().getOptionalServerPlayer().ifPresent(p ->
                        p.sendSystemMessage(Component.literal("Your opponent has changed their team since entering the queue. You have been automatically requeued.").withStyle(s -> s.withColor(0xFF5555)))
                );
                queue.remove(entry1.getPlayer().getId());
                addToQueue(entry1.player.getId(), team1);
            });
            return;
        }
        if (!team1Legal || !team2Legal) {
            // Notify players and do not start the match
            if (!team1Legal) {
                PermissionUtil.getOptionalServerPlayer(match.getPlayer1().getId())
                        .ifPresent(p -> p.sendSystemMessage(Component.literal("Your team is not legal for ranked battle.").withStyle(s -> s.withColor(0xFF5555))));
                queue.remove(entry1.getPlayer().getId());
            }
            if (!team2Legal) {
                PermissionUtil.getOptionalServerPlayer(match.getPlayer2().getId())
                        .ifPresent(p -> p.sendSystemMessage(Component.literal("Your team is not legal for ranked battle.").withStyle(s -> s.withColor(0xFF5555))));
                queue.remove(entry2.getPlayer().getId());
            }
            return;
        }

        // Notify players and tell them the match is starting and who they are facing
        //TODO: send more detailed info about the opponent (ELO, recent win/loss record, etc)
        //TODO: Teleport players to a waiting area or prepare them for battle
        //TODO: Teleport players to the assigned battle arena when the battle starts
        ServerPlayer player1 = match.getPlayer1().getOptionalServerPlayer().get();
        ServerPlayer player2 = match.getPlayer2().getOptionalServerPlayer().get();

        //make players invulnerable for the duration of the battle
        if (!player1.isInvulnerable())
            player1.setInvulnerable(true);
        if (!player2.isInvulnerable())
            player2.setInvulnerable(true);

        //retrieve any pokemon sent out from the party and return them to the party
        Optional<List<Pokemon>> player1PartyPokemon = BattleUtil.getOptionalActivePartyPokemon(player1.getUUID());
        player1PartyPokemon.ifPresent(pokemons -> pokemons.forEach(Pokemon::recall));
        Optional<List<Pokemon>> player2PartyPokemon = BattleUtil.getOptionalActivePartyPokemon(player2.getUUID());
        player2PartyPokemon.ifPresent(pokemons -> pokemons.forEach(Pokemon::recall));

        //teleport players to arena
        arena.teleportPlayersToArena(player1, player2);

        PermissionUtil.getOptionalServerPlayer(match.getPlayer1().getId()).ifPresent(player -> {
            player.sendSystemMessage(Component.literal("You have been matched with an opponent! Starting ranked battle...").withStyle(s -> s.withColor(0x55FF55)));
            player.sendSystemMessage(Component.literal("Your opponent is " + match.getPlayer2().getOptionalServerPlayer().get().getName().plainCopy().getString()).withStyle(s -> s.withColor(0x55FF55)));
            player.sendSystemMessage(Component.literal("Your opponent has an ELO of " + match.getPlayer2().getElo()).withStyle(s -> s.withColor(0x55FF55)));

        });
        PermissionUtil.getOptionalServerPlayer(match.getPlayer2().getId()).ifPresent(player -> {
            player.sendSystemMessage(Component.literal("You have been matched with an opponent! Starting ranked battle...").withStyle(s -> s.withColor(0x55FF55)));
            player.sendSystemMessage(Component.literal("Your opponent is " + match.getPlayer1().getOptionalServerPlayer().get().getName().plainCopy().getString()).withStyle(s -> s.withColor(0x55FF55)));
            player.sendSystemMessage(Component.literal("Your opponent has an ELO of " + match.getPlayer1().getElo()).withStyle(s -> s.withColor(0x55FF55)));
        });


        // start battle with cobblemon
        UUID battleId = match.startMatch();
        rankedBattleTracker.markAsRanked(battleId, match);
        System.out.println("Starting ranked match between " + match.getPlayer1().getId());
        System.out.println(" and " + match.getPlayer2().getId() + " with battle ID " + battleId);

        queue.remove(entry1.getPlayer().getId());
        queue.remove(entry2.getPlayer().getId());
    }

    /**
     * Adds a player to the matchmaking queue.
     * @param playerId The UUID of the player to add.
     */
    public void addToQueue(UUID playerId, List<Pokemon> team) {
        try {
            int elo = WynautRankUp.instance.playerDataManager.getElo(playerId).get();
            synchronized (queue) {
                queue.put(playerId, new PlayerQueueEntry(new Player(playerId, elo), team));
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PlayerQueueEntry> getSortedQueue() {
        return queue.values().stream()
                .sorted(Comparator.comparingInt(e -> e.getPlayer().getElo()))
                .collect(Collectors.toList());
    }

    /**
     * Finds the best match from the queue based on ELO difference.
     * Players are matched if their ELO difference is within 100 points.
     * @return An Optional containing the Match if found, otherwise empty.
     */
    public Optional<Match> findMatch() {
        synchronized (queue) {
            if (queue.size() < 2) return Optional.empty();
            Arena availableArena = WynautRankUp.instance.arenaManager.getAvailableArena();
            if (availableArena == null) return Optional.empty();
            List<PlayerQueueEntry> sortedEntries = queue.values().stream()
                    .sorted(Comparator.comparingInt(e -> e.getPlayer().getElo()))
                    .toList();

            int minDiff = Integer.MAX_VALUE;
            PlayerQueueEntry best1 = null, best2 = null;

            for (int i = 0; i < sortedEntries.size() - 1; i++) {
                PlayerQueueEntry entry1 = sortedEntries.get(i);
                PlayerQueueEntry entry2 = sortedEntries.get(i + 1);
                int diff = Math.abs(entry1.getPlayer().getElo() - entry2.getPlayer().getElo());
                if (diff <= 100 && diff < minDiff) {
                    minDiff = diff;
                    best1 = entry1;
                    best2 = entry2;
                }
            }

            // Check both players for being offline or already in a battle, remove and notify if needed
            if (best1 != null && best2 != null) {
                for (PlayerQueueEntry entry : List.of(best1, best2)) {
                    UUID playerId = entry.getPlayer().getId();
                    Optional<ServerPlayer> optionalPlayer = PermissionUtil.getOptionalServerPlayer(playerId);

                    // Offline check
                    if (optionalPlayer.isEmpty()) {
                        queue.remove(playerId);
                        optionalPlayer.ifPresent(player ->
                                player.sendSystemMessage(Component.literal("You have been removed from the ranked queue because you went offline.").withStyle(s -> s.withColor(0xFF5555)))
                        );
                        return Optional.empty();
                    }

                    // Already in battle check
                    if (Cobblemon.INSTANCE.getBattleRegistry().getBattleByParticipatingPlayerId(playerId) != null) {
                        queue.remove(playerId);
                        optionalPlayer.ifPresent(player ->
                                player.sendSystemMessage(Component.literal("You have been removed from the ranked queue because you are already in a battle.").withStyle(s -> s.withColor(0xFF5555)))
                        );
                        return Optional.empty();
                    }
                }
            }



            if (best1 == null || best2 == null) return Optional.empty();
            Match match = new Match(best1.getPlayer(), best2.getPlayer());
            match.setArena(availableArena); // Assign arena to match
            return Optional.of(match);
        }
    }




    public void clearQueue() {
        queue.clear();
    }

    public RankedBattleTracker getRankedBattleTracker() {
        return rankedBattleTracker;
    }

    public MatchResultRecorder getMatchResultRecorder() {
        return matchResultRecorder;
    }


    public boolean isInQueue(UUID uuid) {
        synchronized (queue) {
            return queue.containsKey(uuid);
        }
    }

    /**
     * Gets a list of players in the queue within the ELO range of the given player.
     * @param uuid The UUID of the player to check against.
     * @return A CompletionStage that will complete with a list of players within the ELO range.
     */
    public CompletionStage<List<Player>> getPlayersInRange(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            int playerElo = fetchPlayerElo(uuid);
            synchronized (queue) {
                return queue.values().stream()
                        .map(PlayerQueueEntry::getPlayer)
                        .filter(p -> !p.getId().equals(uuid)) // Exclude the player themselves
                        .filter(p -> Math.abs(p.getElo() - playerElo) <= 100)
                        .collect(Collectors.toList());
            }
        }, executor);
    }

    /**
     * Fetches the ELO of a player by their UUID.
     * @param uuid The UUID of the player.
     * @return The ELO of the player, or 0 if an error occurs.
     */
    private int fetchPlayerElo(UUID uuid) {
        try {
            return WynautRankUp.instance.playerDataManager.getElo(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return 0; // or handle as appropriate
        }
    }



    /**
     * Removes a player from the queue by their UUID.
     * @param uuid The UUID of the player to remove.
     */
    public void removeFromQueue(UUID uuid) {
        synchronized (queue) {
            queue.remove(uuid);
        }
    }
}
