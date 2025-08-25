package com.example.magiclanterns;

import com.example.magiclanterns.block.ModBlocks;
import com.example.magiclanterns.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagicLanterns implements ModInitializer {
    public static final String MOD_ID = "magiclanterns";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ScreenHandlerType<MagicLanternScreenHandler> MAGIC_LANTERN_HANDLER =
        Registry.register(Registries.SCREEN_HANDLER,
            new Identifier(MOD_ID, "magic_lantern"),
            new ScreenHandlerType<>(MagicLanternScreenHandler::new, FeatureFlagRegistry.DO_NOT_USE));

    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModBlocks.registerBlocks();
        ModBlockEntities.registerBlockEntities();

        ScreenRegistry.register(MAGIC_LANTERN_HANDLER, MagicLanternScreen::new);
    }
}