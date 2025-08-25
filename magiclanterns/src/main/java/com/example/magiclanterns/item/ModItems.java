package com.example.magiclanterns.item;

import com.example.magiclanterns.MagicLanterns;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item CRYSTAL_SHARD = new Item(new FabricItemSettings());

    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier(MagicLanterns.MOD_ID, "crystal_shard"), CRYSTAL_SHARD);
    }
}