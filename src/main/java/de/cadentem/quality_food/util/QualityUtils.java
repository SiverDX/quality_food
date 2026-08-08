package de.cadentem.quality_food.util;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.compat.SpecialContainer;
import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Modification;
import de.cadentem.quality_food.core.Quality;
import net.brdle.collectorsreap.common.block.FruitBushBlock;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.WildCropBlock;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class QualityUtils {
    public static final String QUALITY_TAG = "quality_food";
    public static final String QUALITY_KEY = "quality";
    public static final String EFFECT_TAG = "effects";
    public static final String EFFECT_PROBABILITY_KEY = "chance";

    private static final RandomSource RANDOM = RandomSource.create();

    /**
     * Used for crafting <br>
     * Quality depends on the average weight of the quality from the ingredients
     */
    public static void applyQuality(final ItemStack stack, final Collection<ItemStack> ingredients, @Nullable final Player player) {
        double totalWeight = 0;
        int validIngredients = 0;

        for (ItemStack ingredient : ingredients) {
            if (!Utils.isValidItem(ingredient)) {
                continue;
            }

            totalWeight += QualityConfig.getWeight(getQuality(ingredient));
            validIngredients++;
        }

        if (validIngredients == 0) {
            applyQuality(stack, player);
            return;
        }

        Quality selected = Quality.NONE;
        double averageWeight = totalWeight / validIngredients;

        for (Quality quality : Quality.values()) {
            if (quality.level() == 0) {
                continue;
            }

            double chance = QualityConfig.calculateChance(quality, averageWeight);
            chance = Modification.luck(player).apply(chance);

            if (chance > 0 && chance >= RANDOM.nextDouble()) {
                selected = quality;
            }
        }

        QualityUtils.applyQuality(stack, selected);
    }

    /** Used for block drops */
    public static void applyQuality(final ItemStack stack, final BlockState state, final Quality blockQuality, @Nullable final Player player, @Nullable final BlockState farmland) {
        if (isRelevantCrop(state)) {
            Quality selected = Quality.NONE;

            for (Quality quality : Quality.values()) {
                if (quality.level() == 0) {
                    continue;
                }

                double chance;

                if (blockQuality.level() == 0) {
                    // Weight would be 0, meaning no quality can be calculated
                    chance = QualityConfig.getChance(quality);
                } else {
                    chance = QualityConfig.calculateChance(quality, QualityConfig.getWeight(blockQuality));
                }

                chance = Modification.harvestOrSeedMultiplier(quality, stack).apply(chance);
                chance = Modification.luck(player).apply(chance);
                chance = Modification.farmland(state, farmland).apply(chance);

                if (chance > 0 && chance >= RANDOM.nextDouble()) {
                    selected = quality;
                }
            }

            QualityUtils.applyQuality(stack, selected);
        } else if (isValidQuality(blockQuality)) {
            // The block itself if it has quality
            applyQuality(stack, blockQuality);
        } else if (blockQuality != Quality.NONE_PLAYER_PLACED) {
            // The block itself or harvested items when the crop has no quality
            applyQuality(stack, player);
        }
    }

    /** Generic if no further context is present */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player) {
        Quality selected = Quality.NONE;

        for (Quality quality : Quality.values()) {
            if (quality.level() == 0) {
                continue;
            }

            double chance = RANDOM.nextDouble();
            chance = Modification.luck(player).apply(chance);

            if (chance >= 1 - QualityConfig.getChance(quality)) {
                selected = quality;
            }
        }

        QualityUtils.applyQuality(stack, selected);
    }

    /** Applies the quality if its valid and the item has no existing quality */
    public static void applyQuality(final ItemStack stack, final Quality quality) {
        applyQuality(stack, quality, false);
    }

    /** Applies the quality if its valid (if 'canUpgrade' is set to 'true' it can override the quality if its of a higher level */
    public static void applyQuality(final ItemStack stack, final Quality quality, boolean canUpgrade) {
        if (!isValidQuality(quality) || !Utils.isValidItem(stack)) {
            return;
        }

        if (!canUpgrade && hasQuality(stack) || canUpgrade && getQuality(stack).level() > quality.level()) {
            return;
        }

        CompoundTag qualityTag = new CompoundTag();
        qualityTag.putInt(QUALITY_KEY, quality.level());

        if (stack.getFoodProperties(null) != null) {
            QualityConfig config = ServerConfig.QUALITY_CONFIG.get(quality);
            ListTag effects = new ListTag();

            config.getEffects().forEach(effectConfig -> {
                if (effectConfig.test(stack)) {
                    if (RANDOM.nextDouble() <= effectConfig.getEffect().chance()) {
                        CompoundTag effectTag = new CompoundTag();
                        effectTag.putDouble(EFFECT_PROBABILITY_KEY, effectConfig.getEffect().probability());
                        effects.add(new MobEffectInstance(effectConfig.getEffect().effect(), effectConfig.getEffect().duration(), effectConfig.getEffect().amplifier()).save(effectTag));
                    }
                }
            });

            qualityTag.put(EFFECT_TAG, effects);
        }

        CompoundTag tag = stack.getOrCreateTag();
        tag.put(QUALITY_TAG, qualityTag);
    }

    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    public static boolean isRelevantCrop(final BlockState state) {
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            return true;
        }

        if (Compat.Mod.FARMERSDELIGHT.isLoaded() && state.getBlock() instanceof WildCropBlock) {
            return true;
        }

        if (Compat.Mod.COLLECTORS_REAP.isLoaded() && state.getBlock() instanceof FruitBushBlock && state.getValue(FruitBushBlock.AGE) == FruitBushBlock.MAX_AGE) {
            return true;
        }

        if (Compat.Mod.FARM_AND_CHARM.isLoaded() && state.is(TagKey.create(Registries.BLOCK, Compat.location(Compat.Mod.FARM_AND_CHARM.modid(), "wild_crops")))) {
            return true;
        }

        // TODO :: check

        return false;
    }

    public static void handleConversion(@NotNull final ItemStack result, @NotNull final Container container, @Nullable final Recipe<?> recipe, @Nullable final RegistryAccess access) {
        boolean shouldRetainQuality = ServerConfig.isRetainQualityRecipe(recipe, access);
        boolean handleCompacting = ServerConfig.HANDLE_COMPACTING.get();

        if (!shouldRetainQuality && !handleCompacting) {
            return;
        }

        Pair<HashMap<Item, Integer>, int[]> data = getContainerData(container);

        int relevantItemCount = data.getFirst().entrySet().stream().mapToInt(entry -> {
            if (Utils.isValidItem(entry.getKey().getDefaultInstance())) {
                return entry.getValue();
            }

            return 0;
        }).sum();

        Quality quality = getQuality(data.getSecond(), relevantItemCount);

        if (quality.level() > 0 && (shouldRetainQuality || (getCompactingSize(data.getFirst(), container) == relevantItemCount || /* decompacting */ relevantItemCount == 1 && (result.getCount() == 4 || result.getCount() == 9)))) {
            applyQuality(result, quality);
        }
    }

    private static Pair<HashMap<Item, Integer>, int[]> getContainerData(final Container container) {
        // Collect the amount of qualities present for all items in the container
        int[] qualities = new int[Quality.values().length];
        HashMap<Item, Integer> items = new HashMap<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack containerStack = container.getItem(i);
            Item item = containerStack.getItem();

            if (container instanceof SpecialContainer) {
                items.put(item, items.getOrDefault(item, 0) + containerStack.getCount());
            } else {
                items.put(item, items.getOrDefault(item, 0) + 1);
            }

            if (!Utils.isValidItem(containerStack)) {
                continue;
            }

            if (container instanceof SpecialContainer) {
                qualities[getQuality(containerStack).ordinal()] += containerStack.getCount();
            } else {
                qualities[getQuality(containerStack).ordinal()]++;
            }
        }

        return Pair.of(items, qualities);
    }

    /** Get the most fitting quality (if all items are diamond -> diamond / if 3 are diamond and 6 are gold -> gold) */
    private static Quality getQuality(final int[] qualities, int itemCount) {
        if (itemCount == 0) {
            return Quality.NONE;
        }

        for (int ordinal = Quality.DIAMOND.ordinal(); ordinal > 0; ordinal--) {
            itemCount -= qualities[ordinal];

            if (itemCount <= 0) {
                return Quality.get(ordinal);
            }
        }

        return Quality.NONE;
    }

    private static int getCompactingSize(final HashMap<Item, Integer> items, final Container container) {
        Set<Item> keys = items.keySet();

        if (keys.size() != 1 && !(keys.size() == 2 && keys.contains(Items.AIR))) {
            // Either the crafting container only contains 1 type of item or it contains 2 and the other item is air (i.e. no item)
            return -1;
        }

        int containerSize = container.getContainerSize();

        for (Item key : keys) {
            int itemCount = items.get(key);

            if (container instanceof SpecialContainer) {
                if (key == Items.AIR) {
                    continue;
                }

                // There is probably a better way to check this but not worth the effort at the moment
                if (itemCount == 4 || itemCount == 9) {
                    return itemCount;
                } else {
                    return -1;
                }
            } else {
                if (key == Items.AIR && (containerSize - itemCount - /* 2x2 */ 4 != 0 && containerSize - itemCount - /* 3x3 */ 9 != 0)) {
                    // If the other slots (besides 2x2 / 3x3) are not empty then it's not a valid compacting recipe
                    return -1;
                } else if (key != Items.AIR && (itemCount == /* 2x2 */ 4 || itemCount == /* 3x3 */ 9)) {
                    return itemCount;
                }
            }
        }

        return -1;
    }

    public static float getCookingBonus(final Quality quality) {
        ForgeConfigSpec.DoubleValue config = ServerConfig.COOKING_BONUS.get(quality);

        if (config != null) {
            return config.get().floatValue();
        }

        return switch (quality) {
            case IRON -> 0.35f;
            case GOLD -> 1;
            case DIAMOND -> 2;
            default -> 0;
        };
    }

    public static boolean hasQuality(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        boolean hasTag = stack.getTag() != null && stack.getTag().get(QUALITY_TAG) != null;

        if (!hasTag) {
            return false;
        }

        return stack.getTag().getCompound(QUALITY_TAG).getInt(QUALITY_KEY) != 0;
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

    public static boolean isValidQuality(final Quality quality) {
        return !(quality == null || quality == Quality.NONE || quality == Quality.NONE_PLAYER_PLACED);
    }
}

