package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.compat.Compat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final List<FarmlandConfig> FARMLAND_CONFIG = new ArrayList<>();

    public static final ModConfigSpec.DoubleValue LUCK_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CROP_TARGET_CHANCE;
    public static final ModConfigSpec.DoubleValue SEED_CHANCE_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue QUARK_HANDLE_CONFIG;
    public static final ModConfigSpec.BooleanValue HANDLE_COMPACTING;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> NO_QUALITY_RECIPES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> RETAIN_QUALITY_RECIPES;

    private static final ModConfigSpec.ConfigValue<List<? extends String>> FARMLAND_CONFIG_INTERNAL;
    private static final List<String> NO_QUALITY_RECIPES_DEFAULT = new ArrayList<>();
    private static final List<String> RETAIN_QUALITY_RECIPES_DEFAULT = new ArrayList<>();

    static {
        fillNoQualityRecipes();
        fillRetainQualityRecipes();

        LUCK_MULTIPLIER = BUILDER.comment("Luck will affect how often each quality will be tried for (10 luck * 0.25 multiplier -> 2.5 rolls, meaning 2 rolls and 50% chance for another)").defineInRange("luck_multiplier", 0.25d, 0f, 10);
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
        BUILDER.pop();

        BUILDER.push("Compatibility");
        QUARK_HANDLE_CONFIG = BUILDER.comment("Handle Quark harvest & replant automatically (if you have custom behaviour configured regarding the quality block state turn this off)").define("quark_handle_config", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void reloadConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC && /* Can not be the case when stopping the server? */ SPEC.isLoaded()) {
            FARMLAND_CONFIG.clear();
            FARMLAND_CONFIG_INTERNAL.get().forEach(entry -> FARMLAND_CONFIG.add(new FarmlandConfig(entry)));
            FARMLAND_CONFIG.sort(Comparator.comparingInt(entry -> entry.index));

            QualityFood.LOG.info("Reloaded configuration");
            QualityFood.LOG.info("- Farmland config: {}", FARMLAND_CONFIG);
        }
    }

    public static boolean isNoQualityRecipe(@Nullable final RecipeHolder<?> recipe) {
        if (recipe == null) {
            return false;
        }

        return NO_QUALITY_RECIPES.get().contains(recipe.id().toString());
    }

    public static boolean isRetainQualityRecipe(@Nullable final RecipeHolder<?> recipe) {
        if (recipe == null) {
            return false;
        }

        return RETAIN_QUALITY_RECIPES.get().contains(recipe.id().toString());
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
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/apple_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/beetroot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/beetroot_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/berry_sack").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/berry_sack_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/carrot_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/chorus_fruit_block").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/chorus_fruit_block_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/cocoa_bean_sack").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/cocoa_bean_sack_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/glowberry_sack").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/glowberry_sack_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/golden_apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/golden_apple_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/golden_carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/golden_carrot_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/potato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/potato_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/sugar_cane_block").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.quark("building/crafting/compressed/sugar_cane_block_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("carrot_from_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("potato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("potato_from_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("beetroot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("beetroot_from_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("cabbage_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("cabbage").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("tomato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("tomato").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("onion_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("onion").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("rice_bale").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("rice_panicle").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("rice_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("rice_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("white_grape_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("white_grape").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("red_grape_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("red_grape").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("cherry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("cherries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("apples").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.supplementaries("sugar_cube").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.supplementaries("sugar_cube_uncrafting").toString());
    }

    private static void fillRetainQualityRecipes() {
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:pumpkin_seeds");
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:melon_seeds");
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_sugar_cane");
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_honey_bottle");
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("tomato_seeds").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.farmersdelight("rice").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("seed_from_red_grape").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("seed_from_white_grape").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("seed_from_red_savanna_grape").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("seed_from_white_savanna_grape").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("seed_from_red_taiga_grape").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("seed_from_white_taiga_grape").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("seed_from_red_jungle_grape").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(Compat.vinery("seed_from_white_jungle_grape").toString());
    }
}
