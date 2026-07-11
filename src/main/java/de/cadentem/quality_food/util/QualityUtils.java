package de.cadentem.quality_food.util;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.compat.SpecialContainer;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Modification;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.registry.QFComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.WildCropBlock;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class QualityUtils {
    private static final RandomSource RANDOM = RandomSource.create();

    /**
     * Used for crafting <br>
     * Quality depends on the average weight of the quality from the ingredients
     */
    public static void applyQuality(final ItemStack stack, final Collection<ItemStack> ingredients, @Nullable final Player player, final RegistryAccess access) {
        double totalWeight = 0;
        int validIngredients = 0;

        for (ItemStack ingredient : ingredients) {
            if (!Utils.isValidItem(ingredient)) {
                continue;
            }

            Holder<QualityType> type = QualityUtils.getType(ingredient);

            if (type.value() != QualityType.NONE) {
                totalWeight += type.value().weight();
            }

            validIngredients++;
        }

        if (validIngredients == 0) {
            applyQuality(stack, player, access);
            return;
        }

        Holder<QualityType> selected = null;
        double averageWeight = totalWeight / validIngredients;

        for (Holder<QualityType> type : access.registryOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).holders().toList()) {
            if (selected != null && type.value().level() <= selected.value().level()) {
                continue;
            }

            double chance = QualityUtils.calculateChance(type.value(), averageWeight);
            chance = Modification.luck(player).apply(chance);

            if (chance > 0 && chance >= RANDOM.nextDouble()) {
                selected = type;
            }
        }

        if (selected == null) {
            return;
        }

        QualityUtils.applyQuality(stack, QualityType.createQuality(selected, stack));
    }

    /** Used for block drops */
    public static void applyQuality(final ItemStack stack, final BlockState state, final Quality blockQuality, @Nullable final Player player, @Nullable final BlockState farmland, final RegistryAccess access) {
        if (isRelevantCrop(state)) {
            Holder<QualityType> selected = null;

            for (Holder<QualityType> type : access.registryOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).holders().toList()) {
                if (selected != null && type.value().level() <= selected.value().level()) {
                    continue;
                }

                double chance;

                if (blockQuality == Quality.NONE || blockQuality == Quality.PLAYER_PLACED) {
                    chance = type.value().chance();
                } else {
                    chance = QualityUtils.calculateChance(type.value(), blockQuality.getType().value().weight());
                }

                chance = Modification.harvestOrSeedMultiplier(type, stack).apply(chance);
                chance = Modification.luck(player).apply(chance);
                chance = Modification.farmland(state, farmland).apply(chance);

                if (chance > 0 && chance >= RANDOM.nextDouble()) {
                    selected = type;
                }
            }

            if (selected != null) {
                QualityUtils.applyQuality(stack, selected);
            }
        } else if (isValidQuality(blockQuality)) {
            // The block itself if it has quality
            applyQuality(stack, blockQuality);
        } else if (blockQuality != Quality.PLAYER_PLACED) {
            // The block itself or harvested items when the crop has no quality
            applyQuality(stack, player, access);
        }
    }

    /** Generic if no further context is present */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player, final RegistryAccess access) {
        Holder<QualityType> selected = null;

        for (Holder<QualityType> type : access.registryOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).holders().toList()) {
            if (selected != null && type.value().level() <= selected.value().level()) {
                continue;
            }

            double chance = RANDOM.nextDouble();
            chance = Modification.luck(player).apply(chance);

            if (chance >= 1 - type.value().chance()) {
                selected = type;
            }
        }

        if (selected != null) {
            QualityUtils.applyQuality(stack, selected);
        }
    }

    public static boolean applyQuality(final ItemStack stack, final Holder<QualityType> type) {
        return applyQuality(stack, QualityType.createQuality(type, stack));
    }

    /**
     * @param stack   The item to apply quality to
     * @param quality The quality to directly set
     * @return If the quality was successfully set true, otherwise false
     */
    public static boolean applyQuality(final ItemStack stack, final Quality quality) {
        return applyQuality(stack, quality, false);
    }

    /**
     * @param stack   The item to apply quality to
     * @param quality The quality to directly set
     * @param canUpgrade Allows the quality to override the (potentially) existing quality
     * @return If the quality was successfully set true otherwise false
     */
    public static boolean applyQuality(final ItemStack stack, final Quality quality, boolean canUpgrade) {
        if (!isValidQuality(quality) || !Utils.isValidItem(stack)) {
            return false;
        }

        if (!canUpgrade && QualityUtils.hasQuality(stack) || getQuality(stack).level() > quality.level()) {
            return false;
        }

        stack.set(QFComponents.QUALITY_DATA_COMPONENT, quality);
        return true;
    }

    public static boolean hasQuality(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Quality quality = stack.get(QFComponents.QUALITY_DATA_COMPONENT);

        if (quality == null) {
            return false;
        }

        return quality.level() > 0;
    }

    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    private static boolean isRelevantCrop(final BlockState state) {
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            return true;
        }

        if (Compat.Mod.FARMERSDELIGHT.isLoaded() && state.getBlock() instanceof WildCropBlock) {
            return true;
        }

//        if (Compat.Mod.COLLECTORS_REAP.isLoaded() && state.getBlock() instanceof FruitBushBlock && state.getValue(FruitBushBlock.AGE) == FruitBushBlock.MAX_AGE) {
//            return true;
//        }

        if (Compat.Mod.FARM_AND_CHARM.isLoaded() && state.is(TagKey.create(Registries.BLOCK, Compat.location(Compat.Mod.FARM_AND_CHARM.modid(), "wild_crops")))) {
            return true;
        }

        return false;
    }

    public static void handleConversion(@NotNull final ItemStack result, @NotNull final Container container, @Nullable final RecipeHolder<?> recipe, @Nullable final RegistryAccess access) {
        boolean shouldRetainQuality = ServerConfig.isRetainQualityRecipe(recipe, access);
        boolean handleCompacting = ServerConfig.HANDLE_COMPACTING.get();

        if (!shouldRetainQuality && !handleCompacting) {
            return;
        }

        Pair<HashMap<Item, Integer>, HashMap<Integer, Integer>> data = getContainerData(container);

        int relevantItemCount = data.getFirst().entrySet().stream().mapToInt(entry -> {
            if (Utils.isValidItem(entry.getKey().getDefaultInstance())) {
                return entry.getValue();
            }

            return 0;
        }).sum();

        Quality quality = getQuality(data.getSecond(), relevantItemCount, result);

        if (quality.level() > 0 && (shouldRetainQuality || (getCompactingSize(data.getFirst(), container) == relevantItemCount || /* decompacting */ relevantItemCount == 1 && (result.getCount() == 4 || result.getCount() == 9)))) {
            QualityUtils.applyQuality(result, quality);
        }
    }

    public static double calculateChance(final QualityType quality, final double averageWeight) {
        return Mth.clamp((averageWeight - quality.minWeight()) / (quality.weight() - quality.minWeight()), 0, 1);
    }

    /** Checks if the item already has quality and whether it is a valid item, see {@link Utils#isValidItem(ItemStack)} */
    public static boolean isInvalidItem(final ItemStack stack) {
        return hasQuality(stack) || !Utils.isValidItem(stack);
    }

    private static Pair<HashMap<Item, Integer>, HashMap<Integer, Integer>> getContainerData(final Container container) {
        // Collect the number of qualities present for all items in the container
        // TODO :: hashmap of resourcekey to differentiate qualities of the same level?
        HashMap<Integer, Integer> qualities = new HashMap<>();
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

            Quality quality = QualityUtils.getQuality(containerStack);

            if (container instanceof SpecialContainer) {
                qualities.compute(quality.level(), (key, value) -> value == null ? containerStack.getCount() : value + containerStack.getCount());
            } else {
                qualities.compute(quality.level(), (key, value) -> value == null ? 1 : value + 1);
            }
        }

        return Pair.of(items, qualities);
    }

    /** Get the most fitting quality (if all items are diamond -> diamond / if 3 are diamond and 6 are gold -> gold) */
    private static Quality getQuality(final HashMap<Integer, Integer> qualities, int itemCount, final ItemStack result) {
        if (itemCount == 0) {
            return Quality.NONE;
        }

        List<Integer> levels = qualities.keySet().stream().sorted(Comparator.comparingInt(Integer::intValue).reversed()).toList();

        for (Integer level : levels) {
            itemCount -= qualities.get(level);

            if (itemCount <= 0) {
                // Could result in different effects
                // But the same quality cannot be guaranteed
                // When multiple ingredients are present
                return Quality.getRandom(result, level);
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
        int result = -1;

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
                    // If the other slots (besides 2x2 / 3x3) are not empty, then it's not a valid compacting recipe
                    return -1;
                } else if (key != Items.AIR && (itemCount == /* 2x2 */ 4 || itemCount == /* 3x3 */ 9)) {
                    return itemCount;
                }
            }
        }

        return result;
    }

    /** Returns the {@link Quality} if present, otherwise {@link Quality#NONE} */
    public static Quality getQuality(@Nullable final ItemStack stack) {
        if (stack == null) {
            return Quality.NONE;
        }

        Quality quality = stack.get(QFComponents.QUALITY_DATA_COMPONENT);

        if (quality == null) {
            return Quality.NONE;
        }

        return quality;
    }

    /** Returns the corresponding {@link QualityType} to the {@link Quality} if possible, otherwise {@link QualityType#NONE} */
    public static Holder<QualityType> getType(final ItemStack stack) {
        return QualityUtils.getQuality(stack).getType();
    }

    public static boolean isValidQuality(final Quality quality) {
        return quality != null && quality != Quality.NONE && quality != Quality.PLAYER_PLACED;
    }
}
