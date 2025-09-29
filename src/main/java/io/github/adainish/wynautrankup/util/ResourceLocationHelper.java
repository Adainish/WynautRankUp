package io.github.adainish.wynautrankup.util;

import io.github.adainish.wynautrankup.WynautRankUp;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Locale;
import java.util.Optional;

public class ResourceLocationHelper
{
    public static ResourceLocation of(String resourceLocation) {
        try {
            return ResourceLocation.parse(resourceLocation.toLowerCase(Locale.ROOT));
        } catch (ResourceLocationException var2) {
            return null;
        }
    }
    public static ResourceKey<Level> getDimension(String dimension) {
        if (dimension == null)
            return null;
        return dimension.isEmpty() ? null : getDimension(ResourceLocationHelper.of(dimension));
    }

    public static ResourceKey<Level> getDimension(ResourceLocation key) {
        return ResourceKey.create(Registries.DIMENSION, key);
    }

    public static Optional<ServerLevel> getWorld(ResourceKey<Level> key) {
        return Optional.ofNullable(WynautRankUp.instance.server.getLevel(key));
    }

    public static Optional<Registry<Biome>> getBiomeRegistry() {
        return WynautRankUp.instance.server.registryAccess().registry(Registries.BIOME);
    }

    public static Optional<ServerLevel> getWorld(String key) {
        return getWorld(getDimension(key));
    }
}
