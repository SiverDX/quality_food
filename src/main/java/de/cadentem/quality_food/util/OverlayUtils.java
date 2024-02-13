package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.registry.QFItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class OverlayUtils {
    private static ItemStack IRON_OVERLAY;
    private static ItemStack GOLD_OVERLAY;
    private static ItemStack DIAMOND_OVERLAY;

    public static ItemStack getOverlay(final ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            CompoundTag qualityTag = tag.getCompound(QualityUtils.QUALITY_TAG);
            return getOverlay(qualityTag.getInt(QualityUtils.QUALITY_KEY));
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

    public static boolean isOverlay(final ItemStack stack) {
        return stack == IRON_OVERLAY || stack == GOLD_OVERLAY || stack == DIAMOND_OVERLAY;
    }
}
