package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> NO_QUALITY_RECIPE;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FARMLAND_CONFIG_INTERNAL;
    private static final List<String> NO_QUALITY_RECIPES_DEFAULT = new ArrayList<>();

    static {
        NO_QUALITY_RECIPES_DEFAULT.add("minecraft:hay_block");
        NO_QUALITY_RECIPES_DEFAULT.add("minecraft:wheat");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/apple_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/apple_crate_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/beetroot_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/beetroot_crate_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/berry_sack");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/berry_sack_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/carrot_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/carrot_crate_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/chorus_fruit_block");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/chorus_fruit_block_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/cocoa_bean_sack");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/cocoa_bean_sack_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/glowberry_sack");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/glowberry_sack_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/golden_apple_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/golden_apple_crate_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/golden_carrot_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/golden_carrot_crate_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/potato_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/potato_crate_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/sugar_cane_block");
        NO_QUALITY_RECIPES_DEFAULT.add("quark:building/crafting/compressed/sugar_cane_block_uncompress");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:carrot_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:carrot_from_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:potato_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:potato_from_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:beetroot_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:beetroot_from_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:cabbage_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:cabbage");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:tomato_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:tomato");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:onion_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:onion");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:rice_bale");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:rice_panicle");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:rice_bag");
        NO_QUALITY_RECIPES_DEFAULT.add("farmersdelight:rice_from_bag");
        NO_QUALITY_RECIPES_DEFAULT.add("vinery:white_grape_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("vinery:white_grape");
        NO_QUALITY_RECIPES_DEFAULT.add("vinery:red_grape_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("vinery:red_grape");
        NO_QUALITY_RECIPES_DEFAULT.add("vinery:cherry_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("vinery:cherries");
        NO_QUALITY_RECIPES_DEFAULT.add("vinery:apple_crate");
        NO_QUALITY_RECIPES_DEFAULT.add("vinery:apples");
        NO_QUALITY_RECIPES_DEFAULT.add("supplementaries:sugar_cube");
        NO_QUALITY_RECIPES_DEFAULT.add("supplementaries:sugar_cube_uncrafting");

        LUCK_MULTIPLIER = BUILDER.comment("Luck will affect how often each quality will be tried for (10 luck * 0.25 multiplier -> 2.5 rolls, meaning 2 rolls and 50% chance for another)").defineInRange("luck_multiplier", 0.25d, 0f, 10);
        String cropTargetChanceComment1 = "The chance of quality crops dropping its own quality (also affects other qualities) - It affects a multiplier which is calculated as: <crop_target_chance> / <quality.chance>";
        String cropTargetChanceComment2 = "Meaning for Gold it would result in a multiplier of 20 (0.6 / 0.03) -> The chances for all qualities would then be: 20 * 0.10 (iron) = 2 (100%) | 20 * 0.03 (gold) = 0.6 (60%) | 20 * 0.005 = 0.1 (10%)";
        CROP_TARGET_CHANCE = BUILDER.comment(cropTargetChanceComment1 + cropTargetChanceComment2).defineInRange("crop_target_chance", 0.6d, 0, 1);
        SEED_CHANCE_MULTIPLIER = BUILDER.comment("Multiplier on top of the crop target chance").defineInRange("seed_chance_multiplier", 0.25, 0, 100);
        String farmlandConfigComment1 = "Define multipliers to be applied per farmland on crops - Syntax: <index>;<crop>;<farmland>;<multiplier> (the index defines the sequence in which they will be checked - the first matching one is applied)";
        String farmlandConfigComment2 = "\nExample: [\"2;minecraft:wheat;#farmersdelight:terrain;0.75\", \"3;#minecraft:crops;farmersdelight:rich_soil;1.25\"]";
        FARMLAND_CONFIG_INTERNAL = BUILDER.comment(farmlandConfigComment1 + farmlandConfigComment2).defineList("farmland_config", Collections.emptyList(), ServerConfig::validateFarmlandConfig);
        NO_QUALITY_RECIPE = BUILDER.comment("Define recipes (namespace:path) which should not result in quality being applied (e.g. when the items can be converted back and forth)").defineList("no_quality_recipe", NO_QUALITY_RECIPES_DEFAULT, ServerConfig::validateRecipe);

        for (Quality quality : Quality.values()) {
            if (!QualityUtils.isValidQuality(quality) || quality == Quality.UNDEFINED) {
                continue;
            }

            BUILDER.push(quality.name());

            QualityConfig config = new QualityConfig();
            config.chance = BUILDER.comment("The chance for a quality to occur (with no luck or other bonus)").defineInRange("chance", QualityConfig.getChance(quality), 0, 1);
            config.durationMultiplier = BUILDER.comment("By how much the duration of the effect will get multiplied (beneficial) or divided (harmful) for").defineInRange("duration_multiplier", QualityConfig.getDurationMultiplier(quality), 1, 100);
            config.probabilityAddition = BUILDER.comment("The addition (beneficial) or subtraction (harmful) for the probability (chance for the effect to apply)").defineInRange("probability_addition", QualityConfig.getProbabilityAddition(quality), 0, 1);
            config.amplifierAddition = BUILDER.comment("The addition (beneficial) or subtraction (harmful) for the amplifier (level of the effect)").defineInRange("amplifier_addition", QualityConfig.getAmplifierAddition(quality), 0, 255);
            config.nutritionMultiplier = BUILDER.comment("By how much the nutrition will get multiplied for").defineInRange("nutrition_multiplier", QualityConfig.getNutritionMultiplier(quality), 1, 100);
            config.saturationMultiplier = BUILDER.comment("By how much the saturation will get multiplied for").defineInRange("saturation_multiplier", QualityConfig.getSaturationMultiplier(quality), 1, 100);
            config.effectList = BUILDER.comment("List of effects this rarity can grant (<effect>;<chance>;<duration>;<amplifier>;<probability>)").defineList("effect_list", List.of(), ServerConfig::isEffectListValid);
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

        return NO_QUALITY_RECIPE.get().contains(recipe.getId().toString());
    }

    public static double getFarmlandMultiplier(final BlockState crop, final BlockState farmland) {
        if (crop != null && farmland != null) {
            for (FarmlandConfig farmlandConfig : FARMLAND_CONFIG) {
                if (farmlandConfig.predicate.test(crop, farmland)) {
                    return farmlandConfig.multiplier;
                }
            }
        }

        return -1;
    }

    private static boolean validateRecipe(final Object object) {
        if (object instanceof String string) {
            return ResourceLocation.tryParse(string) != null;
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

            String crop = data[FarmlandConfig.CROP];

            if (ResourceLocation.tryParse(crop.startsWith("#") ? crop.substring(1) : crop) == null) {
                return false;
            }

            String farmland = data[FarmlandConfig.FARMLAND];

            if (ResourceLocation.tryParse(farmland.startsWith("#") ? farmland.substring(1) : farmland) == null) {
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

            if (data.length == 5) {
                if (!ResourceLocation.isValidResourceLocation(data[0])) {
                    return false;
                }

                if (isInvalidChance(/* Chance */ data[1])) {
                    return false;
                }

                if (isInvalidInteger(/* Duration */ data[2])) {
                    return false;
                }

                if (isInvalidInteger(/* Amplifier */ data[3])) {
                    return false;
                }

                return !isInvalidChance(/* Probability */ data[4]);
            }
        }

        return false;
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
}
