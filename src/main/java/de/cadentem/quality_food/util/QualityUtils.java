package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.data.QFItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class QualityUtils {
    public static final String STATE_QUALITY_TAG = "food_quality";
    public static final String QUALITY_TAG = "food_quality.quality";
    public static final String HAS_QUALITY_KEY = "has_quality";
    public static final String QUALITY_KEY = "quality";

    public static boolean hasQuality(final ItemStack stack) {
        boolean hasTag = stack.getTag() != null && stack.getTag().get(QUALITY_TAG) != null;

        if (!hasTag) {
            return false;
        }

        return stack.getTag().getCompound(QUALITY_TAG).getBoolean(HAS_QUALITY_KEY);
    }

    /**
     * @param slots           Slots which may contain crafting materials with quality (will apply a bonus)
     * @param resultSlotIndex The slot to ignore the quality of
     */
    public static float getQualityBonus(final NonNullList<Slot> slots, int resultSlotIndex) {
        float bonus = 0;

        for (Slot slot : slots) {
            if (slot.getSlotIndex() != resultSlotIndex) {
                bonus += QualityUtils.getQuality(slot.getItem()).ordinal() * 3;
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

        applyQuality(stack, player.getRandom(), player.getLuck() + bonus);
    }

    /**
     * @param stack  The item to apply quality to
     * @param player The player whose random variable and luck stat is relevant
     */
    public static void applyQuality(final ItemStack stack, final Player player) {
        if (player.getLevel().isClientSide()) {
            return;
        }

        applyQuality(stack, player.getRandom(), player.getLuck());
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
     * @param luck   Increases the chance to roll quality
     */
    public static void applyQuality(final ItemStack stack, final RandomSource random, float luck) {
        float luckBonus = luck / 100f;

        if (random.nextFloat() <= 0.05f + luckBonus) {
            applyQuality(stack, Quality.DIAMOND);
        } else if (random.nextFloat() <= 0.15f + luckBonus) {
            applyQuality(stack, Quality.GOLD);
        } else if (random.nextFloat() <= 0.25f + luckBonus) {
            applyQuality(stack, Quality.IRON);
        }
    }

    /**
     * @param stack   The item to apply quality to
     * @param quality The quality to directly set ({@link Quality#NONE} is not valid)
     */
    public static void applyQuality(final ItemStack stack, final Quality quality) {
        if (quality == Quality.NONE || hasQuality(stack)) {
            return;
        }

        if (stack.getFoodProperties(null) == null && !Utils.isFeastBlock(stack) && !stack.is(QFItemTags.MATERIAL_WHITELIST)) {
            return;
        }

        CompoundTag qualityTag = new CompoundTag();
        qualityTag.putBoolean(HAS_QUALITY_KEY, true);
        qualityTag.putInt(QUALITY_KEY, quality.ordinal());
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(QUALITY_TAG, qualityTag);
    }

    public static Quality getQuality(final ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            CompoundTag qualityTag = tag.getCompound(QUALITY_TAG);
            boolean hasQuality = qualityTag.getBoolean(HAS_QUALITY_KEY);

            if (hasQuality) {
                return Quality.get(qualityTag.getInt(QUALITY_KEY));
            }
        }

        return Quality.NONE;
    }
}
