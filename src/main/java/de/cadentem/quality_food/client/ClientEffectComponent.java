package de.cadentem.quality_food.client;

import com.mojang.datafixers.util.Either;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.core.EffectComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientEffectComponent implements ClientTooltipComponent {
    private static final DecimalFormat FORMAT = new DecimalFormat("###.##");
    private static final int ICON_SIZE = 12;

    private final MutableComponent text;
    private final Identifier sprite;

    public ClientEffectComponent(final EffectComponent component) {
        ApplyStatusEffectsConsumeEffect effect = (ApplyStatusEffectsConsumeEffect) component.effect();
        MobEffectInstance instance = effect.effects().getFirst();
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

        this.text = Component.translatable("potion.withProbability", effectTooltip, FORMAT.format(effect.probability() * 100) + "%").withStyle(formatting);
        this.sprite = Gui.getMobEffectSprite(instance.getEffect());
    }

    @SubscribeEvent
    public static void addEffectTooltip(final RenderTooltipEvent.GatherComponents event) {
        if (ClientConfig.SPEC.isLoaded() && !ClientConfig.EFFECT_TOOLTIPS.get()) {
            return;
        }

        ItemStack stack = event.getItemStack();
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);
        if (stack.get(DataComponents.FOOD) == null || consumable == null) {
            return;
        }

        for (ConsumeEffect effect : consumable.onConsumeEffects()) {
            if (effect instanceof ApplyStatusEffectsConsumeEffect status) {
                for (MobEffectInstance instance : status.effects()) {
                    event.getTooltipElements().add(Either.right(new EffectComponent(
                            new ApplyStatusEffectsConsumeEffect(List.of(instance), status.probability())
                    )));
                }
            }
        }
    }

    @Override
    public int getHeight(@NotNull final Font font) {
        return ICON_SIZE;
    }

    @Override
    public int getWidth(@NotNull final Font font) {
        return font.width(text) + ICON_SIZE;
    }

    @Override
    public void extractImage(@NotNull final Font font, int x, int y, int width, int height, @NotNull final GuiGraphicsExtractor graphics) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, 9, 9);
    }

    @Override
    public void extractText(@NotNull final GuiGraphicsExtractor graphics, @NotNull final Font font, int mouseX, int mouseY) {
        // Y offset to align the height of the text with the center of the icon
        graphics.text(font, text, mouseX + ICON_SIZE, mouseY + 1, -1, true);
    }
}
