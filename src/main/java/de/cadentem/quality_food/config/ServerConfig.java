package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.StorageRecipeCache;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static de.cadentem.quality_food.compat.Compat.Mod.CRATE_DELIGHT;
import static de.cadentem.quality_food.compat.Compat.Mod.FARMERSDELIGHT;
import static de.cadentem.quality_food.compat.Compat.Mod.HERALBREWS;
import static de.cadentem.quality_food.compat.Compat.Mod.VINTAGEDELIGHT;
import static de.cadentem.quality_food.compat.Compat.location;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final Map<Quality, QualityConfig> QUALITY_CONFIG = new HashMap<>();
    public static final List<FarmlandConfig> FARMLAND_CONFIG = new ArrayList<>();

    public static final ForgeConfigSpec.DoubleValue LUCK_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue CROP_TARGET_CHANCE;
    public static final ForgeConfigSpec.DoubleValue SEED_CHANCE_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue QUARK_HANDLE_CONFIG;
    public static final ForgeConfigSpec.BooleanValue HANDLE_SEED_RECIPES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> NO_QUALITY_RECIPES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> RETAIN_QUALITY_RECIPES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> STORAGE_RECIPE_BLACKLIST;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FARMLAND_CONFIG_INTERNAL;
    private static final List<String> NO_QUALITY_RECIPES_DEFAULT = new ArrayList<>();
    private static final List<String> RETAIN_QUALITY_RECIPES_DEFAULT = new ArrayList<>();

    public static final Map<Quality, ForgeConfigSpec.DoubleValue> COOKING_BONUS = new HashMap<>();

    static {
        fillRetainQualityRecipes();

        LUCK_MULTIPLIER = BUILDER.comment("Luck will affect how often each quality will be tried for (10 luck * 0.1 multiplier -> + 0.75 rolls, resulting in 1 guaranteed (baseline) and 75% chance for another)").defineInRange("luck_multiplier", 0.075d, 0f, 10);
        String cropTargetChanceComment1 = "The chance of quality crops dropping its own quality (also affects other qualities) - It affects a multiplier which is calculated as: <crop_target_chance> / <quality.chance>";
        String cropTargetChanceComment2 = "Meaning for Gold it would result in a multiplier of 20 (0.6 / 0.03) -> The chances for all qualities would then be: 20 * 0.10 (iron) = 2 (100%) | 20 * 0.03 (gold) = 0.6 (60%) | 20 * 0.005 = 0.1 (10%)";
        CROP_TARGET_CHANCE = BUILDER.comment(cropTargetChanceComment1 + cropTargetChanceComment2).defineInRange("crop_target_chance", 0.6d, 0, 1);
        SEED_CHANCE_MULTIPLIER = BUILDER.comment("Multiplier on top of the crop target chance").defineInRange("seed_chance_multiplier", 0.25, 0, 100);
        String farmlandConfigComment1 = "Define multipliers to be applied per farmland on crops - Syntax: <index>;<crop>;<farmland>;<multiplier> (the index defines the sequence in which they will be checked - the first matching one is applied)";
        String farmlandConfigComment2 = "\nExample: [\"2;minecraft:wheat;#farmersdelight:terrain;0.75\", \"3;#minecraft:crops;farmersdelight:rich_soil;1.25\"]";
        FARMLAND_CONFIG_INTERNAL = BUILDER.comment(farmlandConfigComment1 + farmlandConfigComment2).defineList("farmland_config", Collections.emptyList(), ServerConfig::validateFarmlandConfig);

        BUILDER.push("Crafting");
        String noQualityRecipesComment1 = "Define recipes (namespace:path) which should not result in quality being rolled (e.g. when the items can be converted back and forth)";
        String noQualityRecipesComment2 = "\n(Note that without 'handle_compacting' or an addition to 'retain_quality_recipes' storage blocks will not retain their quality)";
        NO_QUALITY_RECIPES = BUILDER.comment(noQualityRecipesComment1 + noQualityRecipesComment2).defineList("no_quality_recipes", NO_QUALITY_RECIPES_DEFAULT, ServerConfig::validateRecipe);
        RETAIN_QUALITY_RECIPES = BUILDER.comment("Define recipes (namespace:path) which should result in the quality being applied to the result (only if all ingredients have the same quality)").defineList("retain_quality_recipes", RETAIN_QUALITY_RECIPES_DEFAULT, ServerConfig::validateRecipe);
        HANDLE_SEED_RECIPES = BUILDER.comment("Attempt to handle recipes involving seed items automatically (to avoid having to add all of them to the retain_quality_recipes config)").define("handle_seed_recipes", true);
        STORAGE_RECIPE_BLACKLIST = BUILDER.comment("Define recipes (namespace:path) which should be excluded from the automatic storage block detection").defineList("storage_recipe_blacklist", Collections.emptyList(), ServerConfig::validateRecipe);
        BUILDER.pop();

        BUILDER.push("Cooking");
        String cookingBonusComment = "The bonus this quality contributes when used as a cooking ingredient";
        String cookingBonusComment1 = "\nThe logic is: <base_chance> + (<sum_of_bonus> / <ingredient_count>) / <quality_level>², which makes higher qualities more rare";
        String cookingBonusComment2 = "\nExample: 3 iron & 1 gold -> x + (2.05 / 4) / y² results in ~16% chance for gold and ~6% chance for diamond ";
        BUILDER.comment(cookingBonusComment + cookingBonusComment1 + cookingBonusComment2);

        for (Quality quality : Quality.values()) {
            if (!QualityUtils.isValidQuality(quality) || quality == Quality.UNDEFINED) {
                continue;
            }

            COOKING_BONUS.put(quality, BUILDER.comment("Bonus for " + quality.name()).defineInRange(quality.name().toLowerCase(Locale.ENGLISH) + "_bonus", QualityUtils.getCookingBonus(quality), 0, 100));
        }

        BUILDER.pop();

        for (Quality quality : Quality.values()) {
            if (!QualityUtils.isValidQuality(quality) || quality == Quality.UNDEFINED) {
                continue;
            }

            BUILDER.push(quality.name());

            QualityConfig config = new QualityConfig();
            config.weight = BUILDER.comment("The weight of the quality (relevant for crafting - average weight from the quality of the ingredients determine affect the resulting quality)").defineInRange("weight", QualityConfig.getWeight(quality), 0, 100);
            config.minWeight = BUILDER.comment("The min. weight of the quality (chance for quality when crafting: (average_weight - min_weight) / (weight / min_weight)").defineInRange("min_weight", QualityConfig.getMinWeight(quality), 0, 100);
            config.chance = BUILDER.comment("The chance for a quality to occur (with no luck or other bonus)").defineInRange("chance", QualityConfig.getChance(quality), 0, 1);
            config.cropMultiplier = BUILDER.comment("A chance multiplier for dropped crops (from a fully grown crop)").defineInRange("crop_multiplier", QualityConfig.getCropMultiplier(quality), 0, 5);
            config.seedMultiplier = BUILDER.comment("A chance multiplier for dropped seeds (from a fully grown crop)").defineInRange("seed_multiplier", QualityConfig.getCropMultiplier(quality), 0, 5);
            config.durationMultiplier = BUILDER.comment("By how much the duration of the effect will get multiplied (beneficial) or divided (harmful) for").defineInRange("duration_multiplier", QualityConfig.getDurationMultiplier(quality), 1, 100);
            config.probabilityAddition = BUILDER.comment("The addition (beneficial) or subtraction (harmful) for the probability (chance for the effect to apply)").defineInRange("probability_addition", QualityConfig.getProbabilityAddition(quality), 0, 1);
            config.amplifierAddition = BUILDER.comment("The addition (beneficial) or subtraction (harmful) for the amplifier (level of the effect)").defineInRange("amplifier_addition", QualityConfig.getAmplifierAddition(quality), 0, 255);
            config.nutritionMultiplier = BUILDER.comment("By how much the nutrition will get multiplied for").defineInRange("nutrition_multiplier", QualityConfig.getNutritionMultiplier(quality), 1, 100);
            config.saturationMultiplier = BUILDER.comment("By how much the saturation will get multiplied for").defineInRange("saturation_multiplier", QualityConfig.getSaturationMultiplier(quality), 1, 100);
            config.effect_list_internal = BUILDER.comment("List of effects this rarity can grant (the item can be a tag) (<item>;<effect>;<chance>;<duration>;<amplifier>;<probability>)").defineList("effect_list", List.of(), ServerConfig::isEffectListValid);
            QUALITY_CONFIG.put(quality, config);
            BUILDER.pop();
        }

        BUILDER.push("Compatibility");
        QUARK_HANDLE_CONFIG = BUILDER.comment("Handle Quark harvest & replant automatically (if you have custom behaviour configured regarding the quality block state turn this off)").define("quark_handle_config", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void reloadConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC && /* Can not be the case when stopping the server? */ SPEC.isLoaded()) {
            QUALITY_CONFIG.values().forEach(QualityConfig::initializeEffects);

            FARMLAND_CONFIG.clear();
            FARMLAND_CONFIG_INTERNAL.get().forEach(entry -> FARMLAND_CONFIG.add(new FarmlandConfig(entry)));
            FARMLAND_CONFIG.sort(Comparator.comparingInt(entry -> entry.index));

            QualityFood.LOG.info("Reloaded configuration");
            QualityFood.LOG.info("- Farmland config: {}", FARMLAND_CONFIG);
        }
    }

    public static boolean isNoQualityRecipe(@Nullable final Recipe<?> recipe, final Level level) {
        if (recipe == null) {
            return false;
        }

        if (StorageRecipeCache.isStorageRecipe(recipe, level)) {
            return true;
        }

        return NO_QUALITY_RECIPES.get().contains(recipe.getId().toString());
    }

    public static boolean isRetainQualityRecipe(@Nullable final Recipe<?> recipe, @Nullable RegistryAccess access) {
        if (recipe == null) {
            return false;
        }

        if (ServerConfig.HANDLE_SEED_RECIPES.get()) {
            if (access == null && ServerLifecycleHooks.getCurrentServer() != null) {
                access = ServerLifecycleHooks.getCurrentServer().registryAccess();
            }

            if (access != null && recipe.getResultItem(access).is(Tags.Items.SEEDS)) {
                return true;
            }
        }

        return RETAIN_QUALITY_RECIPES.get().contains(recipe.getId().toString());
    }

    public static float getFarmlandMultiplier(final BlockState crop, final BlockState farmland) {
        if (crop != null && farmland != null) {
            for (FarmlandConfig farmlandConfig : FARMLAND_CONFIG) {
                if (farmlandConfig.predicate.test(crop, farmland)) {
                    return (float) farmlandConfig.multiplier;
                }
            }
        }

        return 1;
    }

    private static boolean validateRecipe(final Object object) {
        if (object instanceof String string) {
            return ResourceLocation.isValidResourceLocation(string);
        }

        return false;
    }

    private static boolean validateFarmlandConfig(final Object object) {
        if (object instanceof String string) {
            String[] data = string.split(";");

            if (data.length != 4) {
                return false;
            }

            if (isInvalidInteger(data[FarmlandConfig.INDEX])) {
                return false;
            }

            if (isInvalidResource(data[FarmlandConfig.CROP], true)) {
                return false;
            }

            if (isInvalidResource(data[FarmlandConfig.FARMLAND], true)) {
                return false;
            }

            try {
                double multiplier = Double.parseDouble(data[FarmlandConfig.MULTIPLIER]);

                if (multiplier < 0) {
                    return false;
                }
            } catch (NumberFormatException ignored) {
                return false;
            }

            return true;
        }

        return false;
    }

    private static boolean isEffectListValid(final Object object) {
        if (object instanceof String string) {
            String[] data = string.split(";");

            if (data.length != 6) {
                return false;
            }

            if (isInvalidResource(data[EffectConfig.ITEM], true)) {
                return false;
            }

            if (isInvalidResource(data[EffectConfig.EFFECT], false)) {
                return false;
            }

            if (isInvalidChance(data[EffectConfig.CHANCE])) {
                return false;
            }

            if (isInvalidInteger(data[EffectConfig.DURATION])) {
                return false;
            }

            if (isInvalidInteger(data[EffectConfig.AMPLIFIER])) {
                return false;
            }

            return !isInvalidChance(data[EffectConfig.PROBABILITY]);
        }

        return false;
    }

    private static boolean isInvalidResource(final String resource, final boolean allowTags) {
        if (resource == null) {
            return true;
        }

        if (allowTags && resource.startsWith("#")) {
            return !ResourceLocation.isValidResourceLocation(resource.substring(1));
        }

        return !ResourceLocation.isValidResourceLocation(resource);
    }

    private static boolean isInvalidChance(final String value) {
        if (value == null) {
            return true;
        }

        try {
            double chance = Double.parseDouble(value);
            return chance < 0 || chance > 1;
        } catch (NumberFormatException ignored) { /* Nothing to do */ }

        return true;
    }

    private static boolean isInvalidInteger(final String value) {
        if (value == null) {
            return true;
        }

        if (value.contains(".") || value.contains(",")) {
            return true;
        }

        try {
            return Integer.parseInt(value) < 0;
        } catch (NumberFormatException ignored) { /* Nothing to do */ }

        return true;
    }

    /** For weird conversion recipes (e.g., storage-block recipe but contains glass bottles) */
    private static void fillRetainQualityRecipes() {
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_sugar_cane");
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_honey_bottle");
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:honey_bottle");

        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "stacked_melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "stacked_pumpkins").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "pumpkins").toString());
        // Still Crate Delight
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "stacked_melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "stacked_pumpkins").toString());

        // Other
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(VINTAGEDELIGHT.modid(), "honey_jar_deconstruct").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(VINTAGEDELIGHT.modid(), "honey_jar").toString());

        // 2x3 storage block
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "tea_leaf_crate").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "tea_leafs_from_crate").toString());
        // Mixed input items
//        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "mixed_tea_leaf_block").toString());
    }
}
