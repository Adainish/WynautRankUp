package io.github.adainish.wynautrankup.util;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.battles.BattleBuilder;
import com.cobblemon.mod.common.battles.BattleFormat;
import com.cobblemon.mod.common.battles.BattleStartResult;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.data.Player;
import kotlin.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class BattleUtil
{
    public static UUID startShowdownBattle(Player player, Player secondPlayer) {
        AtomicReference<UUID> matchUUID = new AtomicReference<>();
        if (player.getOptionalServerPlayer().isPresent() && secondPlayer.getOptionalServerPlayer().isPresent()) {
            BattleStartResult battleStartResult = BattleBuilder.INSTANCE.pvp1v1(player.getOptionalServerPlayer().get(),
                    secondPlayer.getOptionalServerPlayer().get(),
                    null,
                    null,
                    BattleFormat.Companion.getGEN_9_SINGLES(),
                    true,
                    true,
                    trainer -> {
                        PartyStore party = null;
                        party = Cobblemon.INSTANCE.getStorage().getParty(trainer);
                        return party;
                    });
            battleStartResult.ifSuccessful(pokemonBattle -> {
                try {
                    if (pokemonBattle.getBattleId() != null) {
                        matchUUID.set(pokemonBattle.getBattleId());
                    }
                } catch (Exception ignored)
                {

                }
                return Unit.INSTANCE;
            });
        }
        return matchUUID.get();
    }

    public static Optional<PlayerPartyStore> getOptionalPlayerPartyStore(UUID uuid)
    {
        Optional<PlayerPartyStore> playerPartyStore = Optional.empty();
        PlayerPartyStore party = null;
        party = Cobblemon.INSTANCE.getStorage().getParty(uuid, WynautRankUp.instance.server.registryAccess());
        playerPartyStore = Optional.of(party);
        return playerPartyStore;
    }

    public static Optional<List<Pokemon>> getOptionalActivePartyPokemon(UUID uuid)
    {
        Optional<List<Pokemon>> pokemonList = Optional.empty();
        if (PermissionUtil.getOptionalServerPlayer(uuid).isPresent()) {
            List<Pokemon> pokemonArrayList = new ArrayList<>();
            if (getOptionalPlayerPartyStore(uuid).isPresent()) {
                PlayerPartyStore partyStore = getOptionalPlayerPartyStore(uuid).get();
                partyStore.forEach(pokemon -> {
                    if (pokemon != null)
                        pokemonArrayList.add(pokemon);
                });
                pokemonList = Optional.of(pokemonArrayList);
            }
        }
        return pokemonList;
    }

}
