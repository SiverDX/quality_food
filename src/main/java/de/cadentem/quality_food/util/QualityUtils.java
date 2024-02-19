package de.cadentem.quality_food.util;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Quality;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class QualityUtils {
    public static final String QUALITY_TAG = "quality_food";
    public static final String QUALITY_KEY = "quality";
    public static final String EFFECT_TAG = "effects";
    public static final String EFFECT_PROBABILITY_KEY = "chance";

    private static final RandomSource RANDOM = RandomSource.create();

    public static boolean hasQuality(final ItemStack stack) {
        boolean hasTag = stack.getTag() != null && stack.getTag().get(QUALITY_TAG) != null;

        if (!hasTag) {
            return false;
        }

        return stack.getTag().getCompound(QUALITY_TAG).getInt(QUALITY_KEY) != 0;
    }

    /**
     * @param slots       Slots which may contain crafting materials with quality (will apply a bonus)
     * @param isSlotValid To test whether the slot is relevant or not (since the list usually contains the inventory as well)
     */
    public static float getQualityBonus(final NonNullList<Slot> slots, final Predicate<Slot> isSlotValid) {
        float bonus = 0;

        for (Slot slot : slots) {
            if (isSlotValid.test(slot)) {
                bonus += getBonus(QualityUtils.getQuality(slot.getItem()));
            }
        }

        return bonus;
    }

    /**
     * @param stack  The item to apply quality to
     * @param player The player whose random variable and luck stat is relevant
     * @param bonus  Additional bonus to luck (i.e. higher chance for quality)
     */
    public static void applyQuality(final ItemStack stack, final Player player, float bonus) {
        if (player.getLevel().isClientSide()) {
            return;
        }

        applyQuality(stack, player.getRandom(), player.getLuck() / 100f + bonus);
    }

    /**
     * @param stack  The item to apply quality to
     * @param player The player whose random variable and luck stat is relevant
     */
    public static void applyQuality(final ItemStack stack, final Player player) {
        if (player.getLevel().isClientSide()) {
            return;
        }

        applyQuality(stack, player.getRandom(), player.getLuck() / 100f);
    }

    /**
     * @param stack        The item to apply quality to
     * @param livingEntity The entity whose random variable will be used
     */
    public static void applyQuality(final ItemStack stack, final LivingEntity livingEntity, float bonus) {
        if (livingEntity.getLevel().isClientSide()) {
            return;
        }

        applyQuality(stack, livingEntity.getRandom(), bonus);
    }

    /**
     * @param stack        The item to apply quality to
     * @param livingEntity The entity whose random variable will be used
     */
    public static void applyQuality(final ItemStack stack, final LivingEntity livingEntity) {
        if (livingEntity.getLevel().isClientSide()) {
            return;
        }

        applyQuality(stack, livingEntity.getRandom(), 0);
    }

    /**
     * @param stack The item to apply quality to
     * @param level To check for client side and usage of random
     */
    public static void applyQuality(final ItemStack stack, final Level level) {
        if (level.isClientSide()) {
            return;
        }

        applyQuality(stack, level.getRandom(), 0);
    }

    /**
     * @param stack  The item to apply quality to
     * @param random To calculate the chance
     */
    public static void applyQuality(final ItemStack stack, final RandomSource random) {
        applyQuality(stack, random, 0);
    }

    /**
     * @param stack  The item to apply quality to
     * @param random To calculate the chance
     * @param bonus  Increases the chance to roll quality
     */
    public static void applyQuality(final ItemStack stack, final RandomSource random, float bonus) {
        if (Utils.LAST_STACK.get() == stack) {
            return;
        }

        Utils.LAST_STACK.set(stack);

        bonus = Mth.clamp(bonus, 0, 1);
        float roll = random.nextFloat();

        if (checkAndRoll(stack, roll, bonus, Quality.DIAMOND)) {
            return;
        }

        if (checkAndRoll(stack, roll, bonus, Quality.GOLD)) {
            return;
        }

        if (checkAndRoll(stack, roll, bonus, Quality.IRON)) {
            return;
        }
    }

    private static boolean checkAndRoll(final ItemStack stack, float roll, float bonus, final Quality quality) {
        QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality.ordinal());
        float chance = config != null ? config.chance.get().floatValue() : QualityConfig.getDefaultChance(quality);

        if (roll <= chance + bonus) {
            applyQuality(stack, quality);
            return true;
        }

        return false;
    }

    /**
     * @param stack   The item to apply quality to
     * @param quality The quality to directly set ({@link Quality#NONE} is not valid)
     */
    public static void applyQuality(final ItemStack stack, final Quality quality) {
        if (quality == Quality.NONE || hasQuality(stack) || !Utils.isValidItem(stack)) {
            return;
        }

        CompoundTag qualityTag = new CompoundTag();
        qualityTag.putInt(QUALITY_KEY, quality.ordinal());

        if (stack.getFoodProperties(null) != null) {
            QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality.ordinal());
            ListTag effects = new ListTag();

            config.getEffects().forEach(effect -> {
                if (RANDOM.nextDouble() <= effect.chance()) {
                    CompoundTag effectTag = new CompoundTag();
                    effectTag.putDouble(EFFECT_PROBABILITY_KEY, effect.probability());
                    effects.add(new MobEffectInstance(effect.effect(), effect.duration(), effect.amplifier()).save(effectTag));
                }
            });

            qualityTag.put(EFFECT_TAG, effects);
        }

        CompoundTag tag = stack.getOrCreateTag();
        tag.put(QUALITY_TAG, qualityTag);
    }

    public static float getBonus(final Quality quality) {
        return switch (quality) {
            case NONE -> 0;
            case IRON -> 0.05f;
            case GOLD -> 0.1f;
            case DIAMOND -> 0.15f;
        };
    }

    public static Quality getQuality(@Nullable final ItemStack stack) {
        if (stack == null) {
            return Quality.NONE;
        }

        CompoundTag tag = stack.getTag();

        if (tag != null) {
            CompoundTag qualityTag = tag.getCompound(QUALITY_TAG);
            return Quality.get(qualityTag.getInt(QUALITY_KEY));
        }

        return Quality.NONE;
    }

    public static List<Pair<Double, MobEffectInstance>> getEffects(@Nullable final ItemStack stack) {
        if (stack == null) {
            return List.of();
        }

        List<Pair<Double, MobEffectInstance>> effects = new ArrayList<>();
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            ListTag effectList = tag.getCompound(QUALITY_TAG).getList(EFFECT_TAG, ListTag.TAG_COMPOUND);

            for (int i = 0; i < effectList.size(); i++) {
                CompoundTag effectTag = effectList.getCompound(i);
                double probability = effectTag.getDouble(EFFECT_PROBABILITY_KEY);
                MobEffectInstance effect = MobEffectInstance.load(effectTag);

                effects.add(Pair.of(probability, effect));
            }
        }

        return effects;
    }
}
