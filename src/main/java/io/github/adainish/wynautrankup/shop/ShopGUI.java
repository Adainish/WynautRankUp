package io.github.adainish.wynautrankup.shop;

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
