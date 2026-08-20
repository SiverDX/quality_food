package de.cadentem.quality_food.util;

import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Modification;
import de.cadentem.quality_food.core.Quality;
import net.brdle.collectorsreap.common.block.FruitBushBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.WildCropBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Objects;

@ParametersAreNonnullByDefault
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

            double chance = QualityConfig.getChance(quality) + QualityConfig.calculateChance(quality, averageWeight);
            chance = Modification.luck(player).apply(chance);

            if (chance > 0 && chance >= RANDOM.nextDouble()) {
                selected = quality;
            }
        }

        QualityUtils.applyQuality(stack, selected);
    }

    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.0")
    public static void applyQuality(final ItemStack stack, final BlockState state, final Quality blockQuality, @Nullable final Player player, @Nullable final BlockState farmland) {
        applyHarvestQuality(stack, state, blockQuality, player, farmland);
    }

    public static void applyHarvestQuality(@Nullable final HarvestContext context) {
        if (context == null) {
            return;
        }

        applyHarvestQuality(context.stack(), context.state(), context.quality(), context.player(), context.farmland());
    }

    /**
     * For most parameters see {@link HarvestContext}
     * @param blockQuality The quality of the harvested block, set to {@link Quality#NONE} if not provided
     * */
    public static void applyHarvestQuality(final ItemStack stack, @Nullable final BlockState state, @Nullable Quality blockQuality, @Nullable final Player player, @Nullable final BlockState farmland) {
        blockQuality = Objects.requireNonNullElse(blockQuality, Quality.NONE);

        if (isRelevantCrop(state)) {
            Quality selected = Quality.NONE;

            for (Quality quality : Quality.values()) {
                // If the crop was player-placed, it should be able to roll for quality
                if (quality.level() == 0 || (blockQuality == Quality.NONE && quality.level() > ServerConfig.MAX_NATURAL_HARVEST_QUALITY.get().level())) {
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
            // To get back the planted seed with its quality
            applyQuality(stack, blockQuality);
        } else if (blockQuality != Quality.NONE_PLAYER_PLACED) {
            // Naturally generated crops / fruits (which should never have any quality)
            applyQuality(stack, player, ServerConfig.MAX_NATURAL_HARVEST_QUALITY.get());
        }
    }

    /** Generic if no further context is present */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player) {
        QualityUtils.applyQuality(stack, player, 0);
    }

    /** Generic if no further context is present */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player, final Quality maxQuality) {
        QualityUtils.applyQuality(stack, player, 0, maxQuality);
    }

    /** @param qualityPotential A bonus to the quality roll (generally used for animals that had potential) */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player, double qualityPotential) {
        QualityUtils.applyQuality(stack, player, qualityPotential, Quality.DIAMOND);
    }

    /** @param qualityPotential A bonus to the quality roll (generally used for animals that had potential) */
    public static void applyQuality(final ItemStack stack, @Nullable final Player player, final double qualityPotential, final Quality maxQuality) {
        Quality selected = Quality.NONE;

        for (Quality quality : Quality.values()) {
            if (quality.level() == 0 || quality.level() > maxQuality.level()) {
                continue;
            }

            double chance = QualityConfig.getChance(quality);
            chance = Modification.luck(player).apply(chance);
            chance = Modification.potential(qualityPotential).apply(chance);

            if (chance > 0 && chance >= RANDOM.nextDouble()) {
                selected = quality;
            }
        }

        QualityUtils.applyQuality(stack, selected);
    }

    /** Applies the quality if it's valid and the item has no existing quality */
    public static void applyQuality(final ItemStack stack, final Quality quality) {
        applyQuality(stack, quality, false);
    }

    /** Applies the quality if it's valid (if 'canUpgrade' is set to 'true', it can override the quality if it's of a higher level */
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
    public static boolean isRelevantCrop(@Nullable final BlockState state) {
        if (state == null) {
            return false;
        }

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

    public static void handleConversion(final ItemStack result, final Container container, @Nullable final Recipe<?> recipe, final Level level) {
        boolean shouldRetainQuality = ServerConfig.isRetainQualityRecipe(recipe, level.registryAccess());
        StorageRecipeCache.Entry storage = StorageRecipeCache.get(recipe, level);

        if (!shouldRetainQuality && storage == null) {
            return;
        }

        ContainerData data = getContainerData(container);
        Quality quality = getQuality(data.qualities(), data.relevantItemCount());

        if (quality.level() == 0) {
            return;
        }

        if (shouldRetainQuality) {
            applyQuality(result, quality);
        } else if (data.relevantItemCount() == (storage.packing() ? storage.size() : 1)) {
            applyQuality(result, quality);
        }
    }

    /**
     * @param relevantItemCount The number of items quality can be applied to
     * @param qualities         How often each quality is present, indexed by {@link Quality#ordinal()}
     */
    private record ContainerData(int relevantItemCount, int[] qualities) {}

    private static ContainerData getContainerData(final Container container) {
        // Collect the number of qualities present for all items in the container
        int[] qualities = new int[Quality.values().length];
        int relevantItemCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack containerStack = container.getItem(i);

            if (!Utils.isValidItem(containerStack)) {
                continue;
            }

            qualities[getQuality(containerStack).ordinal()]++;
            relevantItemCount++;
        }

        return new ContainerData(relevantItemCount, qualities);
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

    public static boolean hasQuality(@Nullable final ItemStack stack) {
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

    public static boolean isValidQuality(@Nullable final Quality quality) {
        return !(quality == null || quality == Quality.NONE || quality == Quality.NONE_PLAYER_PLACED);
    }
}

