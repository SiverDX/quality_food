package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.data.QFItemTags;
import de.cadentem.quality_food.registry.QFItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class QualityUtils {
    private static ItemStack IRON_OVERLAY;
    private static ItemStack GOLD_OVERLAY;
    private static ItemStack DIAMOND_OVERLAY;

    public static final String STATE_QUALITY_TAG = "food_quality";
    private static final String QUALITY_TAG = "food_quality.quality";
    private static final String HAS_QUALITY_KEY = "has_quality";
    private static final String QUALITY_KEY = "quality";

    public static boolean hasQuality(final ItemStack stack) {
        boolean hasTag = stack.getTag() != null && stack.getTag().get(QUALITY_TAG) != null;

        if (!hasTag) {
            return false;
        }

        return stack.getTag().getCompound(QUALITY_TAG).getBoolean(HAS_QUALITY_KEY);
    }

    public static float getQualityBonus(final NonNullList<Slot> slots, int resultSlotIndex) {
        float bonus = 0;

        for (Slot slot : slots) {
            if (slot.getSlotIndex() != resultSlotIndex) {
                bonus += QualityUtils.getQuality(slot.getItem()).ordinal() * 3;
            }
        }

        return bonus;
    }

    public static void applyQuality(final ItemStack stack, final RandomSource random) {
        applyQuality(stack, random, 0);
    }

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

    public static double getNutritionMultiplier(final Quality quality) {
        return switch (quality) {
            case IRON -> 1.5;
            case GOLD -> 2;
            case DIAMOND -> 2.5;
            default -> 1;
        };
    }

    public static double getSaturationMultiplier(final Quality quality) {
        return switch (quality) {
            case IRON -> 1.25;
            case GOLD -> 1.5;
            case DIAMOND -> 1.75;
            default -> 1;
        };
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

    public static ItemStack getOverlay(final ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            CompoundTag qualityTag = tag.getCompound(QUALITY_TAG);
            boolean hasQuality = qualityTag.getBoolean(HAS_QUALITY_KEY);

            if (hasQuality) {
                return getOverlay(qualityTag.getInt(QUALITY_KEY));
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack getOverlay(final int ordinal) {
        return getOverlay(Quality.get(ordinal));
    }

    public static ItemStack getOverlay(final Quality quality) {
        return switch (quality) {
            case IRON -> {
                if (IRON_OVERLAY == null) {
                    IRON_OVERLAY = new ItemStack(QFItems.IRON_OVERLAY.get());
                }

                yield IRON_OVERLAY;
            }
            case GOLD -> {
                if (GOLD_OVERLAY == null) {
                    GOLD_OVERLAY = new ItemStack(QFItems.GOLD_OVERLAY.get());
                }

                yield GOLD_OVERLAY;
            }
            case DIAMOND -> {
                if (DIAMOND_OVERLAY == null) {
                    DIAMOND_OVERLAY = new ItemStack(QFItems.DIAMOND_OVERLAY.get());
                }

                yield DIAMOND_OVERLAY;
            }
            default -> ItemStack.EMPTY;
        };
    }
}
