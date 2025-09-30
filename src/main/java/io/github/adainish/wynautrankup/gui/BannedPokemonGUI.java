package io.github.adainish.wynautrankup.gui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.util.Util;
import net.minecraft.server.level.ServerPlayer;

public class BannedPokemonGUI {

    public void open(ServerPlayer player) {
        UIManager.openUIForcefully(player, getBannedPokemonPage());
    }

    public LinkedPage getBannedPokemonPage() {
        ChestTemplate.Builder builder = Util.returnBasicTemplateBuilder();

        return PaginationHelper.createPagesFromPlaceholders(
                builder.build(),
                WynautRankUp.instance.teamValidator.getBannedPokemonButtons(),
                LinkedPage.builder().title(Util.formattedString("&4Banned Pokémon")).template(builder.build())
        );
    }
}