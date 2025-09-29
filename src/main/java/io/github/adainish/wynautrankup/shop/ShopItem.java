package io.github.adainish.wynautrankup.shop;

import net.minecraft.world.item.ItemStack;

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

}
