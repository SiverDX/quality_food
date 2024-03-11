package de.cadentem.quality_food.util;

import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.data.QFItemTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class QualityUtils {
    public static final String QUALITY_TAG = "quality_food";
    public static final String QUALITY_KEY = "quality";
    public static final String EFFECT_TAG = "effects";
    public static final String EFFECT_PROBABILITY_KEY = "chance";

    private static final RandomSource RANDOM = RandomSource.create();

    public static boolean hasQuality(final ItemStack stack) {
        if (stack == null) {
            return false;
        }

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
    public static float getQualityBonus(final List<Slot> slots, final Predicate<Slot> isSlotValid) {
        float bonus = 0;

        for (Slot slot : slots) {
            if (isSlotValid.test(slot)) {
                bonus += getBonus(QualityUtils.getQuality(slot.getItem()));
            }
        }

        return bonus;
    }

    public static void applyQuality(final ItemStack stack) {
        applyQuality(stack, null, Bonus.DEFAULT);
    }

    public static void applyQuality(final ItemStack stack, @NotNull final Bonus bonus) {
        applyQuality(stack, null, bonus);
    }

    public static void applyQuality(final ItemStack stack, @Nullable final Entity entity) {
        applyQuality(stack, entity, Bonus.DEFAULT);
    }

    public static void applyQuality(final ItemStack stack, @Nullable final Entity entity, @NotNull final Bonus bonus) {
        if (Utils.LAST_STACK.get() == stack) {
            return;
        }

        Utils.LAST_STACK.set(stack);

        RandomSource random = entity instanceof LivingEntity livingEntity ? livingEntity.getRandom() : RANDOM;
        double rolls = Math.max(1, entity instanceof Player player ? player.getLuck() * ServerConfig.LUCK_MULTIPLIER.get() : 1);

        if (checkAndRoll(stack, random, bonus, Quality.DIAMOND, rolls)) {
            return;
        }

        if (checkAndRoll(stack, random, bonus, Quality.GOLD, rolls)) {
            return;
        }

        checkAndRoll(stack, random, bonus, Quality.IRON, rolls);
    }

    private static boolean checkAndRoll(final ItemStack stack, @NotNull final RandomSource random, @NotNull final Bonus bonus, final Quality quality, double rolls) {
        float chance = switch (bonus.type()) {
            case ADDITIVE -> QualityConfig.getChance(quality) + bonus.amount();
            case MULTIPLICATIVE -> QualityConfig.getChance(quality) * bonus.amount();
        };

        int fullRolls = (int) rolls;

        for (int i = 0; i < fullRolls; i++) {
            if (random.nextFloat() <= chance) {
                applyQuality(stack, quality);
                return true;
            }
        }

        if (random.nextDouble() <= (rolls - fullRolls) && random.nextFloat() <= chance) {
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
        if (!isValidQuality(quality) || hasQuality(stack) || !Utils.isValidItem(stack)) {
            return;
        }

        CompoundTag qualityTag = new CompoundTag();
        qualityTag.putInt(QUALITY_KEY, quality.level());

        if (stack.getFoodProperties(null) != null) {
            QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);
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

    public static void applyQuality(final ItemStack stack, @NotNull final BlockState state, @Nullable final Player player) {
        Quality quality = state.hasProperty(Utils.QUALITY_STATE) ? Quality.get(state.getValue(Utils.QUALITY_STATE)) : Quality.NONE;

        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            float targetChance = ServerConfig.CROP_TARGET_CHANCE.get().floatValue();

            if (stack.is(Tags.Items.SEEDS)) {
                targetChance *= ServerConfig.SEED_CHANCE_MULTIPLIER.get();
            }

            if (targetChance > 0 && quality.level() > 0) {
                float multiplier = targetChance / QualityConfig.getChance(quality);
                QualityUtils.applyQuality(stack, player, Bonus.multiplicative(multiplier));
            } else {
                QualityUtils.applyQuality(stack, player);
            }
        } else if (QualityUtils.isValidQuality(quality)) {
            QualityUtils.applyQuality(stack, quality);
        } else if (quality != Quality.NONE_PLAYER_PLACED) {
            QualityUtils.applyQuality(stack, player);
        }
    }

    public static void handleConversion(@NotNull final ItemStack result, @NotNull final Container container) {
        if (result == ItemStack.EMPTY) {
            return;
        }

        int[] qualities = new int[Quality.values().length];
        HashMap<Item, Integer> items = new HashMap<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack containerStack = container.getItem(i);
            Item item = containerStack.getItem();
            items.put(item, items.getOrDefault(item, 0) + 1);

            if (containerStack.isEmpty()) {
                continue;
            }

            qualities[QualityUtils.getQuality(containerStack).ordinal()]++;
        }

        int ordinalToUse = Quality.NONE.ordinal();
        int amount = 0;

        for (int ordinal = 0; ordinal < qualities.length; ordinal++) {
            int storedAmount = qualities[ordinal];

            if (storedAmount != 0 && storedAmount >= amount) {
                ordinalToUse = ordinal;
                amount = storedAmount;
            }
        }

        Quality quality = Quality.get(ordinalToUse);

        if (quality.level() == 0) {
            return;
        }

        boolean shouldConvert = false;

        if (isCompacting(items)) {
            if (/* To avoid duplicating quality items */ amount != 9) {
                return;
            } else {
                shouldConvert = true;
            }
        }

        if (shouldConvert || result.is(QFItemTags.RECIPE_CONVERSION) || isDecompacting(items, result, container)) {
            QualityUtils.applyQuality(result, quality);
        }
    }

    private static boolean isCompacting(final HashMap<Item, Integer> items) {
        return items.size() == 1 && items.get(items.keySet().iterator().next()) == /* Are there non 3x3 compacting recipes? */ 9;
    }

    private static boolean isDecompacting(final HashMap<Item, Integer> items, final ItemStack result, final Container container) {
        if (result.getCount() != 9) {
            return false;
        }

        if (items.size() == 2) {
            Set<Item> keys = items.keySet();

            boolean isValid = true;

            for (Item key : keys) {
                if (key != Items.AIR && items.get(key) != 1) {
                    isValid = false;
                } else if (key == Items.AIR && items.get(key) != container.getContainerSize() - 1) {
                    isValid = false;
                }
            }

            return isValid;
        }

        return false;
    }

    public static float getBonus(final Quality quality) {
        return switch (quality) {
            case IRON -> 0.05f;
            case GOLD -> 0.1f;
            case DIAMOND -> 0.15f;
            default -> 0;
        };
    }

    public static float getCookingBonus(final ItemStack stack) {
        Quality quality = getQuality(stack);

        return switch (quality) {
            case IRON -> 1 / 256f;
            case GOLD -> 1 / 128f;
            case DIAMOND -> 1 / 64f;
            default -> 0;
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

    public static int getPlacementQuality(@Nullable final ItemStack stack) {
        Quality quality = getQuality(stack);
        return quality != Quality.NONE ? quality.ordinal() : Quality.NONE_PLAYER_PLACED.ordinal();
    }

    public static boolean isValidQuality(final Quality quality) {
        return !(quality == null || quality == Quality.NONE || quality == Quality.NONE_PLAYER_PLACED);
    }
}
