package io.github.adainish.wynautrankup.shop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.adainish.wynautrankup.WynautRankUp;
import io.github.adainish.wynautrankup.season.Messenger;
import io.github.adainish.wynautrankup.util.ItemStackAdapter;
import io.github.adainish.wynautrankup.util.PermissionUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.world.item.Items.GOLD_INGOT;

public class ShopManager
{
    public String configPath = "config/WynautRankup/shop_items.json";
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .create();

    private Map<String, ShopItem> items = new HashMap<>();

    public static Messenger messenger = new Messenger();

    public void writeDefaultConfig() {
        File file = new File(configPath);
        file.mkdirs();
        if (file.exists()) {
            return;
        }
        List<ShopItem> defaultItems = List.of(
                new ShopItem("example_item_1", "Example Item 1", 100, new ItemStack(Items.DIAMOND, 1)),
                new ShopItem("example_item_2", "Example Item 2", 200, new ItemStack(GOLD_INGOT, 5))
        );
        try (FileWriter writer = new FileWriter(file)) {
            Map<String, ShopItem> defaultMap = new HashMap<>();
            for (ShopItem item : defaultItems) {
                defaultMap.put(item.getId(), item);
            }
            gson.toJson(defaultMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromConfig() {
        File file = new File(configPath);
        if (file.exists()) {
            writeDefaultConfig();
        }
        try (FileReader reader = new FileReader(configPath)) {
            Type type = new TypeToken<Map<String, ShopItem>>(){}.getType();
            Map<String, ShopItem> loadedItems = gson.fromJson(reader, type);
            if (loadedItems != null) {
                items.clear();
                items.putAll(loadedItems);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveToConfig() {
        File file = new File(configPath);
        if (file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(items, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else System.out.println("No shop config file found");
    }


    public void addItem(ShopItem item) { items.put(item.getId(), item); }
    public void removeItem(String id) { items.remove(id); }
    public List<ShopItem> getItems() { return List.copyOf(items.values()); }


    public boolean purchaseItem(UUID playerUuid, String itemId) {
        ShopItem item = items.get(itemId);
        if (item == null) return false;

        ServerPlayer serverPlayer = PermissionUtil.getOptionalServerPlayer(playerUuid).orElse(null);
        if (serverPlayer == null) return false;

        int balance = WynautRankUp.instance.playerDataManager.getBalance(playerUuid.toString());
        if (balance < item.getPrice()) return false;

        WynautRankUp.instance.playerDataManager.adjustBalance(playerUuid.toString(), -item.getPrice());

        messenger.giveItem(serverPlayer, item.getItemStack());
        return true;
    }

    public int getPlayerBalance(UUID playerUuid) {
        return WynautRankUp.instance.playerDataManager.getBalance(playerUuid.toString());
    }
}
