package de.cadentem.quality_food.util;

import de.cadentem.quality_food.compat.Compat;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.WildCropBlock;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

            double chance = type.value().chance() + QualityUtils.calculateChance(type.value(), averageWeight);
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

    /**
     * Used for block drops
     * @deprecated Use {@link QualityUtils#applyHarvestQuality(HarvestContext)} or {@link QualityUtils#applyHarvestQuality(ItemStack, BlockState, Quality, Player, BlockState, RegistryAccess)}
     */
    @Deprecated(forRemoval = true)
    public static void applyQuality(final ItemStack stack, @Nullable final BlockState state, @Nullable final Quality blockQuality, @Nullable final Player player, @Nullable final BlockState farmland, final RegistryAccess access) {
        applyHarvestQuality(stack, state, blockQuality, player, farmland, access);
    }

    /** Used for block drops, see {@link HarvestContext} */
    public static void applyHarvestQuality(@Nullable final HarvestContext context) {
        if (context == null) {
            return;
        }

        applyHarvestQuality(context.stack(), context.state(), context.quality(), context.player(), context.farmland(), context.level().registryAccess());
    }

    /**
     * Used for block drops - for most parameters see {@link HarvestContext}
     * @param blockQuality The quality of the harvested block, set to {@link Quality#NONE} if not provided
     */
    public static void applyHarvestQuality(final ItemStack stack, @Nullable final BlockState state, @Nullable Quality blockQuality, @Nullable final Player player, @Nullable final BlockState farmland, final RegistryAccess access) {
        blockQuality = Objects.requireNonNullElse(blockQuality, Quality.NONE);

        if (isRelevantCrop(state)) {
            Holder<QualityType> selected = null;

            for (Holder<QualityType> type : access.registryOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).holders().toList()) {
                if (selected != null && type.value().level() <= selected.value().level()) {
                    continue;
                }

                // If the crop was player-placed, it should be able to roll for any quality
                if (blockQuality == Quality.NONE && type.value().level() > ServerConfig.MAX_NATURAL_HARVEST_QUALITY_LEVEL.get()) {
                    continue;
                }

                double chance;

                if (!isValidQuality(blockQuality)) {
                    // Weight would be 0, meaning no quality can be calculated
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
            // The block itself if it has quality (e.g. to get back the planted seed with its quality)
            applyQuality(stack, blockQuality);
        } else if (blockQuality != Quality.PLAYER_PLACED) {
            // Naturally generated crops / fruits (which should never have any quality) or the block itself
            applyQuality(stack, player, ServerConfig.MAX_NATURAL_HARVEST_QUALITY_LEVEL.get(), access);
        }
    }

    /** Generic if no further context is present */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player, final RegistryAccess access) {
        applyQuality(stack, player, 0, Integer.MAX_VALUE, access);
    }

    /**
     * Generic if no further context is present
     * @param maxLevel Quality types with a higher level than this will not be rolled for
     */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player, int maxLevel, final RegistryAccess access) {
        applyQuality(stack, player, 0, maxLevel, access);
    }

    /**
     * Generic if no further context is present
     * @param potential The potential (0 - 1) of the source (e.g. an animal), see {@link Modification#potential(double)}
     */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player, double potential, final RegistryAccess access) {
        applyQuality(stack, player, potential, Integer.MAX_VALUE, access);
    }

    /**
     * Generic if no further context is present
     * @param potential The potential (0 - 1) of the source (e.g. an animal), see {@link Modification#potential(double)}
     * @param maxLevel  Quality types with a higher level than this will not be rolled for
     */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player, double potential, int maxLevel, final RegistryAccess access) {
        Holder<QualityType> selected = null;

        for (Holder<QualityType> type : access.registryOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).holders().toList()) {
            if (selected != null && type.value().level() <= selected.value().level()) {
                continue;
            }

            if (type.value().level() > maxLevel) {
                continue;
            }

            double chance = type.value().chance();
            chance = Modification.luck(player).apply(chance);
            chance = Modification.potential(potential).apply(chance);

            if (chance > 0 && chance >= RANDOM.nextDouble()) {
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
    private static boolean isRelevantCrop(@Nullable final BlockState state) {
        if (state == null) {
            return false;
        }

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
        StorageRecipeCache.Entry storage = StorageRecipeCache.get(recipe);

        if (!shouldRetainQuality && storage == null) {
            return;
        }

        ContainerData data = getContainerData(container);
        Quality quality = getQuality(data.qualities(), data.relevantItemCount(), result);

        if (quality.level() <= 0) {
            return;
        }

        if (shouldRetainQuality) {
            applyQuality(result, quality);
        } else if (data.relevantItemCount() == (storage.packing() ? storage.size() : 1)) {
            applyQuality(result, quality);
        }
    }

    public static double calculateChance(final QualityType quality, final double averageWeight) {
        return Mth.clamp((averageWeight - quality.minWeight()) / (quality.weight() - quality.minWeight()), 0, 1);
    }

    /** Checks if the item already has quality and whether it is a valid item, see {@link Utils#isValidItem(ItemStack)} */
    public static boolean isInvalidItem(final ItemStack stack) {
        return hasQuality(stack) || !Utils.isValidItem(stack);
    }

    /**
     * @param relevantItemCount The number of items quality can be applied to
     * @param qualities         How often each quality is present, keyed by {@link Quality#level()}
     */
    private record ContainerData(int relevantItemCount, HashMap<Integer, Integer> qualities) {}

    private static ContainerData getContainerData(final Container container) {
        // Collect the number of qualities present for all items in the container
        // TODO :: hashmap of resourcekey to differentiate qualities of the same level?
        HashMap<Integer, Integer> qualities = new HashMap<>();
        int relevantItemCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack containerStack = container.getItem(i);

            if (!Utils.isValidItem(containerStack)) {
                continue;
            }

            qualities.merge(QualityUtils.getQuality(containerStack).level(), 1, Integer::sum);
            relevantItemCount++;
        }

        return new ContainerData(relevantItemCount, qualities);
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

    public static boolean isValidQuality(@Nullable final Quality quality) {
        return quality != null && quality != Quality.NONE && quality != Quality.PLAYER_PLACED;
    }
}
