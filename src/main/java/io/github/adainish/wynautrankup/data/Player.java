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
