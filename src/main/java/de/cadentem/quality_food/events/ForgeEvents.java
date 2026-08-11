package de.cadentem.quality_food.events;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.capability.AnimalData;
import de.cadentem.quality_food.client.ClientProxy;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.core.EffectComponent;
import de.cadentem.quality_food.util.FoodUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.satisfy.herbalbrews.core.items.DrinkBlockItem;
import net.satisfy.herbalbrews.core.items.FlaskItem;

@Mod.EventBusSubscriber
public class ForgeEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleFishing(final ItemFishedEvent event) {
        if (event.getHookEntity() != null && event.getHookEntity().level().isClientSide()) {
            return;
        }

        event.getDrops().forEach(drop -> QualityUtils.applyQuality(drop, event.getEntity()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleLoot(final LivingDropsEvent event) {
        if (event.getEntity() instanceof Player) {
            // Player death should not grant quality
            return;
        }

        event.getDrops().forEach(drop ->
                QualityUtils.applyQuality(drop.getItem(), event.getSource().getEntity() instanceof Player player ? player : null, AnimalData.getPotential(event.getEntity()))
        );
    }

    @SubscribeEvent // remove existing effect tooltips
    public static void addTooltip(final ItemTooltipEvent event) {
        if (ClientConfig.SPEC.isLoaded() && !ClientConfig.EFFECT_TOOLTIPS.get()) {
            return;
        }

        if (event.getItemStack().isEmpty() || event.getItemStack().getFoodProperties(ClientProxy.getLocalPlayer()) == null) {
            return;
        }

        // TODO :: may remove tooltips that do not come from food properties etc.

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

        if (Compat.Mod.HERALBREWS.isLoaded()) {
            CompoundTag nbt = stack.getTag();

            if (nbt == null) {
                return;
            }

            if (stack.getItem() instanceof FlaskItem && nbt.contains("CustomPotionEffects")) {
                ListTag effects = nbt.getList("CustomPotionEffects", Tag.TAG_COMPOUND);

                for (int i = 0; i < effects.size(); i++) {
                    CompoundTag effect = effects.getCompound(i);
                    MobEffectInstance instance = MobEffectInstance.load(effect);

                    if (instance != null) {
                        event.getTooltipElements().add(Either.right(new EffectComponent(Pair.of(instance, 1f))));
                    }
                }
            } else if (stack.getItem() instanceof DrinkBlockItem && nbt.contains("Effect")) {
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(nbt.getString("Effect")));

                if (effect == null) {
                    return;
                }

                FoodUtils.modifyEffect(new MobEffectInstance(effect, nbt.getInt("EffectDuration"), 0), QualityUtils.getQuality(stack)).ifPresent(instance -> {
                    event.getTooltipElements().add(Either.right(new EffectComponent(Pair.of(instance, 1f))));
                });
            }
        }
    }
}
