package com.example.magiclanterns.block;

import com.example.magiclanterns.MagicLanterns;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<MagicLanternBlockEntity> MAGIC_LANTERN_BE =
        Registry.register(Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MagicLanterns.MOD_ID, "magic_lantern_be"),
            FabricBlockEntityTypeBuilder.create(MagicLanternBlockEntity::new, ModBlocks.MAGIC_LANTERN).build()
        );
}