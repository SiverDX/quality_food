package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static de.cadentem.quality_food.compat.Compat.*;

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
    public static final ForgeConfigSpec.BooleanValue HANDLE_COMPACTING;
    public static final ForgeConfigSpec.BooleanValue HANDLE_SEED_RECIPES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> NO_QUALITY_RECIPES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> RETAIN_QUALITY_RECIPES;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FARMLAND_CONFIG_INTERNAL;
    private static final List<String> NO_QUALITY_RECIPES_DEFAULT = new ArrayList<>();
    private static final List<String> RETAIN_QUALITY_RECIPES_DEFAULT = new ArrayList<>();

    static {
        fillNoQualityRecipes();
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
        NO_QUALITY_RECIPES = BUILDER.comment("Define recipes (namespace:path) which should not result in quality being applied (e.g. when the items can be converted back and forth)").defineList("no_quality_recipes", NO_QUALITY_RECIPES_DEFAULT, ServerConfig::validateRecipe);
        RETAIN_QUALITY_RECIPES = BUILDER.comment("Define recipes (namespace:path) which should result in the quality should be always be applied to the result (only if all ingredients have the same quality)").defineList("retain_quality_recipes", RETAIN_QUALITY_RECIPES_DEFAULT, ServerConfig::validateRecipe);
        HANDLE_COMPACTING = BUILDER.comment("Defines whether (de)compacting should be handled automatically (in terms of retaining quality)").define("handle_compacting", true);
        HANDLE_SEED_RECIPES = BUILDER.comment("Attempt to handle recipes involving seed items automatically (to avoid having to add all of them to the retain_quality_recipes config)").define("handle_seed_recipes", true);
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

    public static boolean isNoQualityRecipe(@Nullable final Recipe<?> recipe) {
        if (recipe == null) {
            return false;
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

    public static float getFarmlandMultiplier(final ItemStack cropItem, final ItemStack farmlandItem) {
        if(!(cropItem.getItem() instanceof BlockItem crop) || !(farmlandItem.getItem() instanceof BlockItem farmland)) return 1;

        return getFarmlandMultiplier(crop.getBlock().defaultBlockState(), farmland.getBlock().defaultBlockState());
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

    private static void fillNoQualityRecipes() {
        NO_QUALITY_RECIPES_DEFAULT.add("minecraft:hay_block");
        NO_QUALITY_RECIPES_DEFAULT.add("minecraft:wheat");

        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/apple_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/beetroot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/beetroot_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/berry_sack").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/berry_sack_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/carrot_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/chorus_fruit_block").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/chorus_fruit_block_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/cocoa_bean_sack").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/cocoa_bean_sack_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/glowberry_sack").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/glowberry_sack_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/golden_apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/golden_apple_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/golden_carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/golden_carrot_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/potato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/potato_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/sugar_cane_block").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK, "building/crafting/compressed/sugar_cane_block_uncompress").toString());

        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "carrot_from_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "potato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "potato_from_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "beetroot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "beetroot_from_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "cabbage_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "cabbage").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "tomato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "tomato").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "onion_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "onion").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "rice_bale").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "rice_panicle").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "rice_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "rice_from_bag").toString());

        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY, "white_grape_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY, "white_grape").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY, "red_grape_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY, "red_grape").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY, "cherry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY, "cherries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY, "apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY, "apples").toString());

        NO_QUALITY_RECIPES_DEFAULT.add(location(SUPPLEMENTARIES, "sugar_cube").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(SUPPLEMENTARIES, "sugar_cube_uncrafting").toString());

        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "apples").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "beetroot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "beetroots").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "berry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "berries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "brown_mushroom_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "brown_mushroom").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "carrots").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "cocoabeans_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "cocoabeans").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "cod_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "cod").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "eggs").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "glowberry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "glowberries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "golden_apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "golden_apple").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "golden_carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "golden_carrot").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "potato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "potatoes").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "red_mushroom_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "red_mushroom").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "salmon_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "salmon").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "stacked_melons").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "melons").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "stacked_pumpkins").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "pumpkins").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "sugar_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "sugar").toString());
        // Still Crate Delight
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "banana_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "bananas").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "caiman_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "caiman_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "crocodile_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "crocodile_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "emu_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "emu_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "platypus_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "platypus_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "terrapin_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("alexsmobs", "terrapin_egg").toString());
        // Still Crate Delight
        NO_QUALITY_RECIPES_DEFAULT.add(location("betterend", "end_fish_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("betterend", "end_fish").toString());
        // Still Crate Delight
        NO_QUALITY_RECIPES_DEFAULT.add(location("create", "wheat_flour_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("create", "wheat_flour").toString());
        // Still Crate Delight
        NO_QUALITY_RECIPES_DEFAULT.add(location("diamond_apples", "diamond_apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("diamond_apples", "diamond_apples").toString());
        // Still Crate Delight
        NO_QUALITY_RECIPES_DEFAULT.add(location("farmersdelight", "stacked_melons").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("farmersdelight", "stacked_pumpkins").toString());
        // Still Crate Delight
        NO_QUALITY_RECIPES_DEFAULT.add(location("jagmkiwis", "kiwi_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("jagmkiwis", "kiwi_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("jagmkiwis", "kiwi_fruit_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("jagmkiwis", "kiwi_fruit").toString());
        // Still Crate Delight
        NO_QUALITY_RECIPES_DEFAULT.add(location("naturalist", "bass_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("naturalist", "bass").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("naturalist", "catfish_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("naturalist", "catfish").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("naturalist", "duck_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("naturalist", "duck_eggs").toString());
        // Still Crate Delight
        NO_QUALITY_RECIPES_DEFAULT.add(location("nutritious_feast", "blueberry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location("nutritious_feast", "blueberries").toString());

        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "lettuce_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "lettuce").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "tomato_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "tomato").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "carrot_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "carrot_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "potato_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "potato_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "onion_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "onion_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "beetroot_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "beetroot_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "corn_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "corn_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "strawberry_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "strawberry_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "flour_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "flour_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "oat_ball").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "oat_from_ball").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "barley_ball").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM, "barley_from_ball").toString());
    }

    private static void fillRetainQualityRecipes() {
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_sugar_cane");
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_honey_bottle");

        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "stacked_melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "stacked_pumpkins").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT, "pumpkins").toString());
        // Still Crate Delight
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "stacked_melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT, "stacked_pumpkins").toString());
    }
}
