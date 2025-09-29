package io.github.adainish.wynautrankup.data;

import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.adainish.wynautrankup.util.BattleUtil;
import io.github.adainish.wynautrankup.util.PermissionUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Player {
    private final UUID id;
    private final int elo;

    public Player(UUID id, int elo) {
        this.id = id;
        this.elo = elo;
    }

    public UUID getId() {
        return id;
    }

    public int getElo() {
        return elo;
    }

    public List<Pokemon> getCurrentPartyTeam() {
        return getOptionalServerPlayer().map(player -> {
            Optional<List<Pokemon>> partyOpt = BattleUtil.getOptionalActivePartyPokemon(player.getUUID());
            return partyOpt.orElseGet(ArrayList::new);
        }).orElseGet(ArrayList::new);
    }


    public Optional<ServerPlayer> getOptionalServerPlayer() {
        return PermissionUtil.getOptionalServerPlayer(id);
    }
    public void sendMessage(String message) {
        getOptionalServerPlayer().ifPresent(player -> player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.AQUA)));
    }
}
