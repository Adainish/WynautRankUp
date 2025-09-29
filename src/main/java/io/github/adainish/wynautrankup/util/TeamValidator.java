package io.github.adainish.wynautrankup.util;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.adainish.wynautrankup.data.Player;
import io.github.adainish.wynautrankup.validator.BannedPokemonRule;
import io.github.adainish.wynautrankup.validator.TeamValidationConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TeamValidator {
    private TeamValidationConfig config;

    public void setConfig(TeamValidationConfig cfg) {
        config = cfg.loadFromFile();
    }

    public boolean isTeamLegal(List<Pokemon> team) {
        boolean isLegal = true;
        if (team == null || team.size() != 6 || config == null) {
            if (config == null)
            {
                System.out.println("[DEBUG] Config is null.");
            }
            if (team == null)
            {
                System.out.println("[DEBUG] Team is null.");
            }
            System.out.println("[DEBUG] Team is null, not 6 Pokémon, or config is missing.");
            isLegal = false;
            return isLegal;
        }
        for (Pokemon p : team) {
            for (BannedPokemonRule rule : config.bannedPokemon) {
                if (matchesRule(p, rule)) {
                    System.out.println("[DEBUG] Illegal Pokémon found: " + p.getSpecies().getName() +
                            " (Form: " + p.getForm().getName() +
                            ", Ability: " + p.getAbility().getName() +
                            ", Held Item: " + (p.getHeldItem$common().isEmpty() ? "None" : BuiltInRegistries.ITEM.getKey(p.getHeldItem$common().getItem())) +
                            ", Moves: " + p.getMoveSet().getMoves().stream().map(m -> m.getName()).toList() +
                            ") matches rule: " + rule);
                    isLegal = false;
                }
            }
        }
        return isLegal;
    }

    public List<String> getTeamIllegality(List<Pokemon> team) {
        List<String> reasons = new ArrayList<>();
        if (team == null) {
            reasons.add("Team is null.");
            return reasons;
        }
        if (team.size() != 6) {
            reasons.add("Team must have exactly 6 Pokémon.");
        }
        if (config == null) {
            reasons.add("Validation config is not set up by admins. Please contact them.");
            return reasons;
        }
        team.forEach(p -> {
            config.bannedPokemon.stream().map(rule -> getIllegalityReasons(p, rule)).forEach(reasons::addAll);
        });
        return reasons;
    }

    private List<String> getIllegalityReasons(Pokemon p, BannedPokemonRule rule) {
        List<String> reasons = new ArrayList<>();
        String name = p.getSpecies().getName();
        if (name.equalsIgnoreCase(rule.species)) {
            reasons.add(name + " is a banned species.");
        }
        if (p.getForm().getName().equalsIgnoreCase(rule.form)) {
            reasons.add(name + " has banned form: " + rule.form);
        }
        if (p.getAbility().getName().equalsIgnoreCase(rule.ability)) {
            reasons.add(name + " has banned ability: " + rule.ability);
        }
        if (rule.heldItem != null) {
            ItemStack held = p.getHeldItem$common();
            String resourceId = BuiltInRegistries.ITEM.getKey(held.getItem()).toString();
            if (resourceId.equalsIgnoreCase(rule.heldItem)) {
                reasons.add(name + " is holding banned item: " + rule.heldItem);
            }
        }
        if (!rule.moves.isEmpty()) {
            for (String move : rule.moves) {
                boolean found = p.getMoveSet().getMoves().stream().anyMatch(m -> m.getName().equalsIgnoreCase(move));
                if (found) {
                    reasons.add(name + " knows banned move: " + move);
                }
            }
        }
        return reasons;
    }


    private boolean matchesRule(Pokemon p, BannedPokemonRule rule) {
        if (rule.species != null && p.getSpecies().getName().equalsIgnoreCase(rule.species)) {
            return true;
        }
        if (rule.form != null && p.getForm().getName().equalsIgnoreCase(rule.form)) {
            return true;
        }
        if (rule.ability != null && p.getAbility().getName().equalsIgnoreCase(rule.ability)) {
            return true;
        }
        if (rule.heldItem != null) {
            ItemStack held = p.getHeldItem$common();
            String resourceId = BuiltInRegistries.ITEM.getKey(held.getItem()).toString();
            if (resourceId.equalsIgnoreCase(rule.heldItem)) {
                return true;
            }
        }
        if (!rule.moves.isEmpty()) {
            for (String move : rule.moves) {
                boolean found = p.getMoveSet().getMoves().stream().anyMatch(m -> m.getName().equalsIgnoreCase(move));
                if (found) {
                    return true;
                }
            }
        }
        return false;
    }



    public boolean doTeamsMatch(Player player, List<Pokemon> team2) {
        List<Pokemon> team1 = player.getCurrentPartyTeam();
        if (team1.size() != team2.size()) return false;
        for (Pokemon p1 : team1) {
            boolean found = false;
            for (Pokemon p2 : team2) {
                boolean sameSpecies = p1.getSpecies().getName().equalsIgnoreCase(p2.getSpecies().getName());
                boolean sameForm = p1.getForm().getName().equalsIgnoreCase(p2.getForm().getName());
                boolean sameAbility = p1.getAbility().getName().equalsIgnoreCase(p2.getAbility().getName());
                boolean sameHeldItem = p1.getHeldItem$common().getItem() == p2.getHeldItem$common().getItem();

                List<String> moves1 = p1.getMoveSet().getMoves().stream().map(Move::getName).toList();
                List<String> moves2 = p2.getMoveSet().getMoves().stream().map(Move::getName).toList();
                boolean sameMoves = moves1.size() == moves2.size() && new HashSet<>(moves1).containsAll(moves2);

                if (sameSpecies && sameForm && sameAbility && sameHeldItem && sameMoves) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    public List<String> getTeamMismatchReasons(Player player, List<Pokemon> team2) {
        List<String> reasons = new ArrayList<>();
        List<Pokemon> team1 = player.getCurrentPartyTeam();
        if (team1.size() != team2.size()) {
            reasons.add("Team size mismatch: " + team1.size() + " vs " + team2.size());
            return reasons;
        }
        for (int i = 0; i < team1.size(); i++) {
            Pokemon p1 = team1.get(i);
            Pokemon p2 = team2.get(i);
            String prefix = "Slot " + (i + 1) + ": ";
            if (!p1.getSpecies().getName().equalsIgnoreCase(p2.getSpecies().getName()))
                reasons.add(prefix + "Species mismatch (" + p1.getSpecies().getName() + " vs " + p2.getSpecies().getName() + ")");
            if (!p1.getForm().getName().equalsIgnoreCase(p2.getForm().getName()))
                reasons.add(prefix + "Form mismatch (" + p1.getForm().getName() + " vs " + p2.getForm().getName() + ")");
            if (!p1.getAbility().getName().equalsIgnoreCase(p2.getAbility().getName()))
                reasons.add(prefix + "Ability mismatch (" + p1.getAbility().getName() + " vs " + p2.getAbility().getName() + ")");
            if (p1.getHeldItem$common().getItem() != p2.getHeldItem$common().getItem())
                reasons.add(prefix + "Held item mismatch");
            List<String> moves1 = p1.getMoveSet().getMoves().stream().map(Move::getName).toList();
            List<String> moves2 = p2.getMoveSet().getMoves().stream().map(Move::getName).toList();
            if (moves1.size() != moves2.size() || !new HashSet<>(moves1).containsAll(moves2))
                reasons.add(prefix + "Moves mismatch (" + moves1 + " vs " + moves2 + ")");
        }
        return reasons;
    }


}
