package de.cadentem.quality_food.events;

import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEvents {
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

        if (attacker == null) {
            return;
        }

        float luck = attacker instanceof Player player ? player.getLuck() : 0;
        RandomSource random = attacker instanceof LivingEntity livingAttacker ? livingAttacker.getRandom() : attacker.level().getRandom();
        event.getDrops().forEach(drop -> QualityUtils.applyQuality(drop.getItem(), random, luck));
    }

    @SubscribeEvent
    public static void handleCrafting(final PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        int size = event.getInventory().getContainerSize();
        float qualityBonus = 0;

        for (int slot = 0; slot < size; slot++) {
            qualityBonus += QualityUtils.getBonus(QualityUtils.getQuality(event.getInventory().getItem(slot)));
        }

        QualityUtils.applyQuality(event.getCrafting(), event.getEntity(), qualityBonus);
    }
}
