package de.cadentem.quality_food.events;

import com.mojang.datafixers.util.Either;
import de.cadentem.quality_food.client.ClientProxy;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.core.EffectComponent;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.text.DecimalFormat;

@EventBusSubscriber
public class GameEvents {
    private static final DecimalFormat FORMAT = new DecimalFormat("###.##");

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

    @SubscribeEvent // remove existing effect tooltips
    public static void addTooltip(final ItemTooltipEvent event) {
        if (ClientConfig.SPEC.isLoaded() && !ClientConfig.EFFECT_TOOLTIPS.get()) {
            return;
        }

        for (Component component : event.getToolTip()) {
            if (component instanceof MutableComponent mutable && mutable.getContents() instanceof TranslatableContents contents && contents.getKey().equals("potion.withDuration")) {
                event.getToolTip().remove(component);
                return;
            }
        }
    }

    @SubscribeEvent
    public static void addEffectTooltip(final RenderTooltipEvent.GatherComponents event) {
        if (ClientConfig.SPEC.isLoaded() && !ClientConfig.EFFECT_TOOLTIPS.get()) {
            return;
        }

        ItemStack stack = event.getItemStack();

        if (stack.isEmpty()) {
            return;
        }

        FoodProperties properties = stack.getFoodProperties(ClientProxy.getLocalPlayer());

        if (properties == null) {
            return;
        }

        for (FoodProperties.PossibleEffect possibleEffect : properties.effects()) {
            event.getTooltipElements().add(Either.right(new EffectComponent(possibleEffect)));
        }
    }
}
