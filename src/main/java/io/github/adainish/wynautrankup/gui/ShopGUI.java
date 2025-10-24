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
package io.github.adainish.wynautrankup.gui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.util.Util;
import net.minecraft.server.level.ServerPlayer;

public class ShopGUI
{

    public void open(ServerPlayer player)
    {
        UIManager.openUIForcefully(player, getShopPage());
    }

    public LinkedPage getShopPage()
    {
        ChestTemplate.Builder builder = Util.returnBasicTemplateBuilder();

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), WynautRankUp.instance.shopManager.getShopItemButtons(), LinkedPage.builder().title(Util.formattedString("")).template(builder.build()));
    }
}
