package de.cadentem.quality_food.client;

import de.cadentem.quality_food.core.EffectComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.text.DecimalFormat;

public class ClientEffectComponent implements ClientTooltipComponent {
    private static final DecimalFormat FORMAT = new DecimalFormat("###.##");
    private static final int ICON_SIZE = 12;

    private final MutableComponent text;
    private final TextureAtlasSprite sprite;

    public ClientEffectComponent(final EffectComponent component) {
        MobEffectInstance instance = component.possibleEffect().effect();
        MutableComponent effectTooltip = Component.translatable(instance.getDescriptionId());

        if (instance.getAmplifier() > 0) {
            effectTooltip = Component.translatable("potion.withAmplifier", effectTooltip, Component.translatable("potion.potency." + instance.getAmplifier()));
        }

        if (instance.getDuration() > 20) {
            ClientLevel level = Minecraft.getInstance().level;
            float tickRate = level != null ? level.tickRateManager().tickrate() : 20f;
            effectTooltip = Component.translatable("potion.withDuration", effectTooltip, MobEffectUtil.formatDuration(instance, 1, tickRate));
        }

        ChatFormatting formatting = instance.getEffect().value().getCategory().getTooltipFormatting();

        this.text = Component.translatable("potion.withProbability", effectTooltip, FORMAT.format(component.possibleEffect().probability() * 100) + "%").withStyle(formatting);
        this.sprite = Minecraft.getInstance().getMobEffectTextures().get(instance.getEffect());
    }

    @Override
    public int getHeight() {
        return ICON_SIZE;
    }

    @Override
    public int getWidth(@NotNull final Font font) {
        return font.width(text) + ICON_SIZE;
    }

    @Override
    public void renderImage(@NotNull final Font font, int x, int y, @NotNull final GuiGraphics graphics) {
        graphics.blit(x, y, 0, 9, 9, sprite);
    }

    @Override
    public void renderText(@NotNull final Font font, int mouseX, int mouseY, @NotNull final Matrix4f matrix, @NotNull final MultiBufferSource.BufferSource bufferSource) {
        // Y offset to align the height of the text with the center of the icon
        font.drawInBatch(text, mouseX + ICON_SIZE, mouseY + 1, -1, true, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
    }
}
