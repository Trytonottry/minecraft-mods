package com.example.magiclanterns.screen;

import com.example.magiclanterns.MagicLanterns;
import com.example.magiclanterns.block.MagicLanternBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MagicLanternScreen extends HandledScreen<MagicLanternScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(MagicLanterns.MOD_ID, "textures/gui/magic_lantern.png");

    public MagicLanternScreen(MagicLanternScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        // Показываем уровень энергии
        MagicLanternBlockEntity be = (MagicLanternBlockEntity) this.handler.inventory;
        double ratio = be.getEnergyStorage().getFillRatio();
        String energy = String.format("Energy: %.1f%%", ratio * 100);
        context.drawText(textRenderer, energy, x + 50, y + 20, 0xFFFFFF, false);
    }
}