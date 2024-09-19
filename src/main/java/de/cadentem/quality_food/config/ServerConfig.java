package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.util.RecipeExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.jetbrains.annotations.NotNull;
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
    public static final ModConfigSpec.BooleanValue HANDLE_COMPACTING;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> NO_QUALITY_RECIPES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> RETAIN_QUALITY_RECIPES;

    private static final ModConfigSpec.ConfigValue<List<? extends String>> FARMLAND_CONFIG_INTERNAL;
    private static final List<String> NO_QUALITY_RECIPES_DEFAULT = new ArrayList<>();
    private static final List<String> RETAIN_QUALITY_RECIPES_DEFAULT = new ArrayList<>();

    private static MinecraftServer server;

    static {
        fillNoQualityRecipes();
        fillRetainQualityRecipes();

        LUCK_MULTIPLIER = BUILDER.comment("Luck will affect how often each quality will be tried for (10 luck * 0.25 multiplier -> 2.5 rolls, meaning 2 rolls and 50% chance for another)").defineInRange("luck_multiplier", 0.25d, 0f, 10);
        String cropTargetChanceComment = "The chance of quality crops dropping it's own quality (also affects other qualities)\nExample for Gold (20 -> 0.6 / 0.03) the chances for all qualities would then be: 20 * 0.10 (iron) = 2 (100%) | 20 * 0.03 (gold) = 0.6 (60%) | 20 * 0.005 = 0.1 (10%)";
        CROP_TARGET_CHANCE = BUILDER.comment(cropTargetChanceComment).defineInRange("crop_target_chance", 0.6d, 0, 1);
        SEED_CHANCE_MULTIPLIER = BUILDER.comment("Multiplier on top of the crop target chance").defineInRange("seed_chance_multiplier", 0.25, 0, 100);
        String farmlandConfigComment = "Define multipliers to be applied per farmland on crops - Syntax: <index>;<crop>;<farmland>;<multiplier>\n(the index defines the sequence in which they will be checked - the first matching one is applied)";
        FARMLAND_CONFIG_INTERNAL = BUILDER.comment(farmlandConfigComment).defineList("farmland_config", Collections::emptyList, () -> "<index>;<crop>;<farmland>;<multiplier>", ServerConfig::validateFarmlandConfig);

        BUILDER.push("Crafting");
        NO_QUALITY_RECIPES = BUILDER.comment("Define recipes (namespace:path) which should not result in quality being applied (e.g. when the items can be converted back and forth)").defineList("no_quality_recipes", () -> NO_QUALITY_RECIPES_DEFAULT, () -> "<namespace>:<path>", ServerConfig::validateRecipe);
        RETAIN_QUALITY_RECIPES = BUILDER.comment("Define recipes (namespace:path) which should result in the quality should be always be applied to the result (only if all ingredients have the same quality)").defineList("retain_quality_recipes", () -> RETAIN_QUALITY_RECIPES_DEFAULT, () -> "<namespace>:<path>", ServerConfig::validateRecipe);
        HANDLE_COMPACTING = BUILDER.comment("Defines whether (de)compacting should be handled automatically (in terms of retaining quality)").define("handle_compacting", true);
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

            if (server != null) {
                server.getRecipeManager().getRecipes().forEach(recipe -> {
                    RecipeExtension extension = (RecipeExtension) (Object) recipe;
                    extension.quality_food$setStatus(RecipeExtension.QualityFoodStatus.NOT_INITIALIZED);
                });
            }
        }
    }

    public static void storeServer(final ServerStartedEvent event) {
        server = event.getServer();
    }

    public static boolean isNoQualityRecipe(@Nullable final RecipeHolder<?> recipe) {
        if (recipe == null) {
            return false;
        }

        RecipeExtension.QualityFoodStatus status = getRecipeStatus(recipe);
        return status == RecipeExtension.QualityFoodStatus.NO_QUALITY || status == RecipeExtension.QualityFoodStatus.NO_QUALITY_AND_RETAIN_QUALITY;
    }

    public static boolean isRetainQualityRecipe(@Nullable final RecipeHolder<?> recipe) {
        if (recipe == null) {
            return false;
        }

        RecipeExtension.QualityFoodStatus status = getRecipeStatus(recipe);
        return status == RecipeExtension.QualityFoodStatus.RETAIN_QUALITY || status == RecipeExtension.QualityFoodStatus.NO_QUALITY_AND_RETAIN_QUALITY;
    }

    private static RecipeExtension.QualityFoodStatus getRecipeStatus(@NotNull final RecipeHolder<?> recipe) {
        RecipeExtension extension = (RecipeExtension) (Object) recipe;

        if (extension.quality_food$getStatus() == RecipeExtension.QualityFoodStatus.NOT_INITIALIZED) {
            boolean isNoQualityRecipe = NO_QUALITY_RECIPES.get().contains(recipe.id().toString());
            boolean isRetainQualityRecipe = RETAIN_QUALITY_RECIPES.get().contains(recipe.id().toString());

            if (isNoQualityRecipe && isRetainQualityRecipe) {
                extension.quality_food$setStatus(RecipeExtension.QualityFoodStatus.NO_QUALITY_AND_RETAIN_QUALITY);
            } else if (isNoQualityRecipe) {
                extension.quality_food$setStatus(RecipeExtension.QualityFoodStatus.NO_QUALITY);
            } else if (isRetainQualityRecipe) {
                extension.quality_food$setStatus(RecipeExtension.QualityFoodStatus.RETAIN_QUALITY);
            } else {
                extension.quality_food$setStatus(null);
            }
        }

        return extension.quality_food$getStatus();
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
