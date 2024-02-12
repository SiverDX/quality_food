package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.Rarity;
import de.cadentem.quality_food.data.QFItemTags;
import de.cadentem.quality_food.registry.QFItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RarityUtils {
    private static ItemStack IRON_OVERLAY;
    private static ItemStack GOLD_OVERLAY;
    private static ItemStack DIAMOND_OVERLAY;

    public static final String STATE_RARITY_TAG = "quality_crops_rarity";
    private static final String RARITY_TAG = "quality_crops.rarity";
    private static final String HAS_RARITY_KEY = "has_rarity";
    private static final String RARITY_KEY = "rarity";

    public static boolean hasRarity(final ItemStack stack) {
        boolean hasTag = stack.getTag() != null && stack.getTag().get(RARITY_TAG) != null;

        if (!hasTag) {
            return false;
        }

        return stack.getTag().getCompound(RARITY_TAG).getBoolean(HAS_RARITY_KEY);
    }

    public static float getRarityBonus(final NonNullList<Slot> slots, int resultSlotIndex) {
        float bonus = 0;

        for (Slot slot : slots) {
            if (slot.getSlotIndex() != resultSlotIndex) {
                bonus += RarityUtils.getRarity(slot.getItem()).ordinal() * 3;
            }
        }

        return bonus;
    }

    public static void applyRarity(final ItemStack stack, final RandomSource random) {
        applyRarity(stack, random, 0);
    }

    public static void applyRarity(final ItemStack stack, final RandomSource random, float luck) {
        float luckBonus = luck / 100f;

        if (random.nextFloat() <= 0.05f + luckBonus) {
            applyRarity(stack, Rarity.DIAMOND);
        } else if (random.nextFloat() <= 0.15f + luckBonus) {
            applyRarity(stack, Rarity.GOLD);
        } else if (random.nextFloat() <= 0.25f + luckBonus) {
            applyRarity(stack, Rarity.IRON);
        }
    }

    public static void applyRarity(final ItemStack stack, final Rarity rarity) {
        if (rarity == Rarity.NONE || hasRarity(stack)) {
            return;
        }

        if (stack.getFoodProperties(null) == null && !Utils.isFeastBlock(stack) && !stack.is(QFItemTags.MATERIAL_WHITELIST)) {
            return;
        }

        CompoundTag rarityTag = new CompoundTag();
        rarityTag.putBoolean(HAS_RARITY_KEY, true);
        rarityTag.putInt(RARITY_KEY, rarity.ordinal());
        CompoundTag tag = stack.getOrCreateTag();
        tag.put(RARITY_TAG, rarityTag);
    }

    public static double getNutritionMultiplier(final Rarity rarity) {
        return switch (rarity) {
            case IRON -> 1.5;
            case GOLD -> 2;
            case DIAMOND -> 2.5;
            default -> 1;
        };
    }

    public static double getSaturationMultiplier(final Rarity rarity) {
        return switch (rarity) {
            case IRON -> 1.25;
            case GOLD -> 1.5;
            case DIAMOND -> 1.75;
            default -> 1;
        };
    }

    public static Rarity getRarity(final ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            CompoundTag rarityTag = tag.getCompound(RARITY_TAG);
            boolean hasRarity = rarityTag.getBoolean(HAS_RARITY_KEY);

            if (hasRarity) {
                return Rarity.get(rarityTag.getInt(RARITY_KEY));
            }
        }

        return Rarity.NONE;
    }

    public static ItemStack getOverlay(final ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            CompoundTag rarityTag = tag.getCompound(RARITY_TAG);
            boolean hasRarity = rarityTag.getBoolean(HAS_RARITY_KEY);

            if (hasRarity) {
                return getOverlay(rarityTag.getInt(RARITY_KEY));
            }
        }

        return ItemStack.EMPTY;
    }

    public static ItemStack getOverlay(final int ordinal) {
        return getOverlay(Rarity.get(ordinal));
    }

    public static ItemStack getOverlay(final Rarity rarity) {
        return switch (rarity) {
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
