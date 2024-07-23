package de.cadentem.quality_food.events;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.text.DecimalFormat;
import java.util.List;

@EventBusSubscriber
public class GameEvents {
    private static final DecimalFormat FORMAT = new DecimalFormat("###.##");

    @SubscribeEvent
    public static void handleRightClick(final PlayerInteractEvent.RightClickBlock event) {
        BlockPos position = event.getHitVec().getBlockPos();

        if (event.getLevel().getBlockEntity(position) != null) {
            Utils.BLOCK_ENTITY_POSITION.set(position);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleFishing(final ItemFishedEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        event.getDrops().forEach(drop -> QualityUtils.applyQuality(drop, event.getEntity()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleLoot(final LivingDropsEvent event) {
        Entity attacker = event.getSource().getEntity();

        if (attacker == null || /* Player death should not grant quality */ event.getEntity() instanceof Player) {
            return;
        }

        if (attacker instanceof LivingEntity livingAttacker) {
            event.getDrops().forEach(drop -> QualityUtils.applyQuality(drop.getItem(), livingAttacker));
        }
    }

    @SubscribeEvent
    public static void addTooltip(final ItemTooltipEvent event) {
        if (ClientConfig.SPEC.isLoaded() && !ClientConfig.EFFECT_TOOLTIPS.get()) {
            return;
        }

        FoodProperties foodProperties = event.getItemStack().getFoodProperties(event.getEntity());

        if (foodProperties != null) {
            List<FoodProperties.PossibleEffect> effectData = foodProperties.effects();

            for (FoodProperties.PossibleEffect data : effectData) {
                MobEffectInstance effect = data.effect();
                MutableComponent effectTooltip = Component.translatable(effect.getDescriptionId());

                if (effect.getAmplifier() > 0) {
                    effectTooltip = Component.translatable("potion.withAmplifier", effectTooltip, Component.translatable("potion.potency." + effect.getAmplifier()));
                }

                if (effect.getDuration() > 20) {
                    effectTooltip = Component.translatable("potion.withDuration", effectTooltip, MobEffectUtil.formatDuration(effect, 1));
                }

                ChatFormatting formatting = effect.getEffect().getCategory().getTooltipFormatting();
                event.getToolTip().remove(effectTooltip.withStyle(formatting));
                effectTooltip = Component.translatable("potion.withProbability", effectTooltip, FORMAT.format(data.probability() * 100) + "%").withStyle(formatting);

                if (!event.getToolTip().contains(effectTooltip)) {
                    event.getToolTip().add(effectTooltip);
                }
            }
        }
    }
}
