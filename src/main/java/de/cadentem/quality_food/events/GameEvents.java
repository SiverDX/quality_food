package de.cadentem.quality_food.events;

import com.mojang.datafixers.util.Either;
import de.cadentem.quality_food.client.ClientProxy;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.core.EffectComponent;
import de.cadentem.quality_food.util.FoodUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.satisfy.herbalbrews.core.items.DrinkBlockItem;
import net.satisfy.herbalbrews.core.items.FlaskItem;

@EventBusSubscriber
public class GameEvents {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleFishing(final ItemFishedEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        event.getDrops().forEach(drop -> QualityUtils.applyQuality(drop, event.getEntity(), event.getEntity().registryAccess()));
    }

    // TODO :: would need to find a way to check the recipe that was used
//    @SubscribeEvent
//    public static void handleCrafting(final PlayerEvent.ItemCraftedEvent event) {
//        if (QualityUtils.isInvalidItem(event.getCrafting())) {
//            return;
//        }
//
//        List<ItemStack> ingredients = new ArrayList<>();
//
//        for (int slot = 0; slot < event.getInventory().getContainerSize(); slot++) {
//            ItemStack ingredient = event.getInventory().getItem(slot);
//
//            if (!ingredient.isEmpty()) {
//                ingredients.add(ingredient);
//            }
//        }
//
//        QualityUtils.applyQuality(event.getCrafting(), ingredients, event.getEntity(), event.getEntity().registryAccess());
//    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleLoot(final LivingDropsEvent event) {
        Entity attacker = event.getSource().getEntity();

        if (attacker == null || /* Player death should not grant quality */ event.getEntity() instanceof Player) {
            return;
        }

        if (attacker instanceof LivingEntity livingAttacker) {
            event.getDrops().forEach(drop -> QualityUtils.applyQuality(drop.getItem(), livingAttacker instanceof Player player ? player : null, livingAttacker.registryAccess()));
        }
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

        for (FoodProperties.PossibleEffect possibleEffect : properties.effects()) {
            event.getTooltipElements().add(Either.right(new EffectComponent(possibleEffect)));
        }

        if (Compat.Mod.HERALBREWS.isLoaded()) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);

            if (contents == null) {
                return;
            }

            if (stack.getItem() instanceof FlaskItem) {
                contents.getAllEffects().forEach(instance -> event.getTooltipElements().add(
                        Either.right(new EffectComponent(new FoodProperties.PossibleEffect(() -> instance, 1f))))
                );
            } else if (stack.getItem() instanceof DrinkBlockItem) {
                // TODO :: modify the potion contents on craft instead of this + effect mod. on appl.
                contents.getAllEffects().forEach(instance -> FoodUtils.modifyEffect(instance, QualityUtils.getType(stack).value())
                        .ifPresent(modifiedInstance -> event.getTooltipElements().add(
                                Either.right(new EffectComponent(new FoodProperties.PossibleEffect(() -> modifiedInstance, 1f)))
                        ))
                );
            }
        }
    }
}
