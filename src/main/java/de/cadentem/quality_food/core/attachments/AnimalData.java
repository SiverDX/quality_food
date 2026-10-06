package de.cadentem.quality_food.core.attachments;

import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.data.QFEntityTypeTags;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/** Stores the quality potential (0 - 1) of an animal, which increases the odds for quality on its drops / produce */
@ParametersAreNonnullByDefault
@EventBusSubscriber
public class AnimalData implements INBTSerializable<CompoundTag> {
    public double potential;

    // TODO :: Also store applied qualities to have a bias for the highest applied

    /** @return The data of the mob or null if the mob is blacklisted (see {@link QFEntityTypeTags#ANIMAL_DATA_BLACKLIST}) */
    public static @Nullable AnimalData get(@Nullable final Entity entity) {
        if (!(entity instanceof Mob mob)) {
            return null;
        }

        if (mob.getType().is(QFEntityTypeTags.ANIMAL_DATA_BLACKLIST)) {
            return null;
        }

        return mob.getData(AttachmentHandler.ANIMAL_DATA);
    }

    public static double getPotential(@Nullable final Entity entity) {
        AnimalData data = get(entity);

        if (data == null) {
            return 0;
        }

        return data.potential;
    }

    public static void feed(final ItemStack food, final Mob mob) {
        if (mob.level().isClientSide()) {
            return;
        }

        Holder<QualityType> type = QualityUtils.getType(food);

        if (type.value() == QualityType.NONE) {
            return;
        }

        AnimalData data = get(mob);

        if (data == null) {
            return;
        }

        double bonus = type.value().potentialBonus();

        if (mob.isBaby()) {
            bonus *= ServerConfig.CHILD_POTENTIAL_GROWTH_MULTIPLIER.get();
        }

        data.potential = Mth.clamp(data.potential + bonus, 0, 1);
    }

    @SubscribeEvent
    public static void inheritPotential(final BabyEntitySpawnEvent event) {
        AgeableMob child = event.getChild();

        if (child == null) {
            return;
        }

        AnimalData childData = get(child);

        if (childData == null) {
            return;
        }

        double averagePotential = (getPotential(event.getParentA()) + getPotential(event.getParentB())) / 2d;
        double randomFactor = child.getRandom().nextDouble() * ServerConfig.CHILD_POTENTIAL_RANDOM_FACTOR_AMOUNT.get();

        childData.potential = Mth.clamp(averagePotential * (ServerConfig.CHILD_POTENTIAL_BASE_PERCENTAGE.get() + randomFactor), 0, 1);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("potential", potential);
        return tag;
    }

    @Override
    public void deserializeNBT(final HolderLookup.Provider provider, final CompoundTag tag) {
        potential = tag.getDouble("potential");
    }
}
