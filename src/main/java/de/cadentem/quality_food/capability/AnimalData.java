package de.cadentem.quality_food.capability;

import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AnimalData {
    public double potential;

    // TODO :: In 1.21.1 also store applied qualities to have a bias for the highest applied

    public static double getPotential(final Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return 0;
        }

        return AnimalDataProvider.getCapability(mob).map(data -> data.potential).orElse(0d);
    }

    public static void feed(final ItemStack food, final Mob mob) {
        Quality quality = QualityUtils.getQuality(food);

        if (quality.level() == 0) {
            return;
        }

        AnimalDataProvider.getCapability(mob).ifPresent(data -> {
            double bonus = QualityConfig.getPotentialBonus(quality);

            if (mob.isBaby()) {
                bonus *= ServerConfig.CHILD_POTENTIAL_GROWTH_MULTIPLIER.get();
            }

            data.potential = Mth.clamp(data.potential + bonus, 0, 1);
        });
    }

    @SubscribeEvent
    public static void applyQuality(final BabyEntitySpawnEvent event) {
        AgeableMob child = event.getChild();

        if (child == null) {
            return;
        }

        AnimalDataProvider.getCapability(child).ifPresent(childData -> {
            Mob firstParent = event.getParentA();
            Mob secondParent = event.getParentB();

            double firstPotential = AnimalDataProvider.getCapability(firstParent)
                    .map(data -> data.potential).orElse(0d);

            double secondPotential = AnimalDataProvider.getCapability(secondParent)
                    .map(data -> data.potential).orElse(0d);

            double averagePotential = (firstPotential + secondPotential) / 2d;
            double randomFactor = child.getRandom().nextDouble() * ServerConfig.CHILD_POTENTIAL_RANDOM_FACTOR_AMOUNT.get();

            childData.potential = Mth.clamp(averagePotential * (ServerConfig.CHILD_POTENTIAL_BASE_PERCENTAGE.get() + randomFactor), 0, 1);
        });
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("potential", potential);
        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        potential = tag.getDouble("potential");
    }
}
