package io.github.adainish.wynautrankup.util;

import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.adainish.wynautrankup.WynautRankUp;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Util
{
    public static String formattedString(String s) {
        return s.replaceAll("&", "§");
    }

    public static Holder<Enchantment> getEnchantment(ResourceKey<Enchantment> enchantmentResourceKey) {
        return WynautRankUp.instance.server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantmentResourceKey);
    }
    public static List<String> formattedArrayList(List<String> list) {

        return list.stream().map(Util::formattedString).collect(Collectors.toList());
    }

    public static List<Component> formattedComponentList(List<String> s) {
        List<Component> list = new ArrayList<>();
        for (String str : s)
            list.add(Component.literal(formattedString(str)));
        return list;
    }

    public static GooeyButton filler() {
        return GooeyButton.builder()
                .display(new ItemStack(Items.GRAY_STAINED_GLASS_PANE))
                .build();
    }

    public static ItemStack pokemonIcon(Pokemon pokemon) {
        return PokemonItem.from(pokemon, 1);
    }

    public static ChestTemplate.Builder returnBasicTemplateBuilder() {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .with(DataComponents.CUSTOM_NAME, TextUtil.parseNativeText("Previous Page"))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .with(DataComponents.CUSTOM_NAME, TextUtil.parseNativeText("Next Page"))
                .linkType(LinkType.Next)
                .build();

        builder.set(0, 3, previous)
                .set(0, 5, next)
                .rectangle(1, 1, 3, 7, placeHolderButton);
        return builder;
    }
}
