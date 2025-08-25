package com.example.magiclanterns.block;

import com.example.magiclanterns.MagicLanterns;
import com.example.magiclanterns.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LanternBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

// Замени тип MAGIC_LANTERN на BlockWithEntity
public static final Block MAGIC_LANTERN = new BlockWithEntity(
    FabricBlockSettings.copyOf(Blocks.LANTERN).luminance(15)
) {
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MagicLanternBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MagicLanternBlockEntity) {
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof MagicLanternBlockEntity lantern) {
            return (int) ((lantern.energy / (double) MagicLanternBlockEntity.MAX_ENERGY) * 15);
        }
        return 0;
    }
};

public class ModBlocks {
    public static final Block MAGIC_LANTERN = new LanternBlock(
        FabricBlockSettings.copyOf(Blocks.LANTERN)
            .luminance(15)
            .sounds(BlockSoundGroup.LANTERN)
            .requiresTool()
    );

    public static void registerBlocks() {
        registerBlock("magic_lantern", MAGIC_LANTERN);
    }

    private static void registerBlock(String name, Block block) {
        Registry.register(Registries.BLOCK, new Identifier(MagicLanterns.MOD_ID, name), block);
        Registry.register(Registries.ITEM, new Identifier(MagicLanterns.MOD_ID, name),
            new BlockItem(block, new Item.Settings()));
    }
}