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
package io.github.adainish.wynautrankup.util;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.lang.reflect.Type;

/**
 * A type adapter for {@link ItemStack}
 * <p> This class is used to serialize and deserialize {@link ItemStack} objects.
 * </p>
 * @Author Adainish
 */
public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    /**
     * Deserialize the Json data to an ItemStack
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context The context of the deserialization
     * @return The deserialized ItemStack
     * @throws JsonParseException If the Json data is invalid
     */
    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return new ItemStack(Items.PAPER);
        }
        if (json.isJsonObject()) {
            return ItemStack.CODEC.parse(JsonOps.INSTANCE, json).result().orElse(new ItemStack(Items.PAPER));
        }
        throw new JsonParseException("Invalid ItemStack JSON: " + json);
    }


    /**
     * Serialize the ItemStack to Json
     *
     * @param src       the object that needs to be converted to Json.
     * @param typeOfSrc the actual type (fully genericized version) of the source object.
     * @param context   The context of the serialization
     * @return The serialized JsonElement
     */
    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.isEmpty()) {
            return context.serialize("", String.class);
        }
        return ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, src).result().orElse(JsonNull.INSTANCE);
    }
}
