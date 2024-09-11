package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.compat.Compat;
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
    public static final ForgeConfigSpec.BooleanValue HANDLE_COMPACTING;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> NO_QUALITY_RECIPES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> RETAIN_QUALITY_RECIPES;
    // public static final ForgeConfigSpec.ConfigValue<List<? extends String>> RETAIN_QUALITY_RECIPES_COOKING;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FARMLAND_CONFIG_INTERNAL;
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
        // RETAIN_QUALITY_RECIPES_COOKING = BUILDER.comment("Defines cooking (furnace etc.) recipes (namespace:path) which should result in the quality should be always be applied to the result (only if all ingredients have the same quality)").defineList("retain_quality_recipes_cooking", List.of(), ServerConfig::validateRecipe);
        HANDLE_COMPACTING = BUILDER.comment("Defines whether (de)compacting should be handled automatically (in terms of retaining quality)").define("handle_compacting", true);
        BUILDER.pop();

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
            String craftingBonusComment1 = "Additive bonus to the chance an ingredient gives (when crafting through a crafting table)";
            String craftingBonusComment2 = "\nThis value is divided by the amount of ingredient types (which can have quality) (i.e. 1x diamond & 1x no quality -> total bonus of 0.35 (if diamond provides a bonus of 0.7))";
            config.craftingBonus = BUILDER.comment(craftingBonusComment1 + craftingBonusComment2).defineInRange("crating_bonus", QualityConfig.getCraftingBonus(quality), 0, 1);
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

    public static boolean isRetainQualityRecipe(@Nullable final Recipe<?> recipe) {
        if (recipe == null) {
            return false;
        }

        return RETAIN_QUALITY_RECIPES.get().contains(recipe.getId().toString());
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

            String crop = data[FarmlandConfig.CROP];

            if (!ResourceLocation.isValidResourceLocation(crop.startsWith("#") ? crop.substring(1) : crop)) {
                return false;
            }

            String farmland = data[FarmlandConfig.FARMLAND];

            if (!ResourceLocation.isValidResourceLocation(farmland.startsWith("#") ? farmland.substring(1) : farmland)) {
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

            if (!ResourceLocation.isValidResourceLocation(data[EffectConfig.ITEM])) {
                return false;
            }

            if (!ResourceLocation.isValidResourceLocation(data[EffectConfig.EFFECT])) {
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

        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("apples").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("beetroot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("beetroots").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("berry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("berries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("brown_mushroom_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("brown_mushroom").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("carrots").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("cocoabeans_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("cocoabeans").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("cod_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("cod").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("eggs").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("glowberry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("glowberries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("golden_apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("golden_apple").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("golden_carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("golden_carrot").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("potato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("potatoes").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("red_mushroom_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("red_mushroom").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("salmon_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("salmon").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("sugar_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.cratedelight("sugar").toString());
        // Still Crate Delight
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("create", "wheat_flour_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("create", "wheat_flour").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("jagmkiwis", "kiwi_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("jagmkiwis", "kiwi_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("jagmkiwis", "kiwi_fruit_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("jagmkiwis", "kiwi_fruit").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("nutritious_feast", "blueberry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("nutritious_feast", "blueberries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("naturalist", "bass_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("naturalist", "bass").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("naturalist", "catfish_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("naturalist", "catfish").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("naturalist", "duck_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("naturalist", "duck_eggs").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "banana_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "bananas").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "caiman_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "caiman_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "crocodile_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "crocodile_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "emu_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "emu_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "platypus_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "platypus_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "terrapin_egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("alexsmobs", "terrapin_egg").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("betterend", "end_fish_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(Compat.location("betterend", "end_fish").toString());
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
