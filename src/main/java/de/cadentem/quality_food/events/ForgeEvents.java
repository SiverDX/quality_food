package de.cadentem.quality_food.events;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.client.ClientProxy;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.core.EffectComponent;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;

@Mod.EventBusSubscriber
public class ForgeEvents {
    private static final DecimalFormat FORMAT = new DecimalFormat("###.##");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleFishing(final ItemFishedEvent event) {
        if (event.getHookEntity() != null && event.getHookEntity().level().isClientSide()) {
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

        for (Pair<MobEffectInstance, Float> possibleEffect : properties.getEffects()) {
            event.getTooltipElements().add(Either.right(new EffectComponent(possibleEffect)));
        }
    }
}
