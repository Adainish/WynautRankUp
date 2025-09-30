package io.github.adainish.wynautrankup.shop;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.util.TextUtil;
import io.github.adainish.wynautrankup.util.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.Arrays;

public class ShopItem
{
    private String id;
    private String displayName;
    private int price;
    private ItemStack itemStack;

    public ShopItem() {}

    public ShopItem(String id, String displayName, int price, ItemStack itemStack) {
        this.id = id;
        this.displayName = displayName;
        this.price = price;
        this.itemStack = itemStack;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public ItemStack getItemStack() {
        return itemStack;
    }
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Button getShopItemButton()
    {
        return GooeyButton.builder()
                .display(getItemStack().copy())
                .with(DataComponents.CUSTOM_NAME, TextUtil.parseNativeText(getDisplayName()))
                .with(DataComponents.LORE, new ItemLore(Util.formattedComponentList(
                        Arrays.asList( "&7Price: &6" + getPrice(),
                                "&eClick to purchase")
                )))
                .onClick(b -> {
                    if (WynautRankUp.instance.shopManager.purchaseItem(b.getPlayer().getUUID(), this.id))
                    {
                        ShopManager.messenger.notify(b.getPlayer(), "You have purchased: " + getDisplayName() + " for " + getPrice() + " coins.");
                    } else {
                        ShopManager.messenger.notify(b.getPlayer(), "You do not have enough coins to purchase: " + getDisplayName() + ".");
                    }
                })
                .build();
    }

}
