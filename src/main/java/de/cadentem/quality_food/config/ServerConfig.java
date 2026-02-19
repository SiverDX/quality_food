package de.cadentem.quality_food.config;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.util.RecipeExtension;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static de.cadentem.quality_food.compat.Compat.*;
import static de.cadentem.quality_food.compat.Compat.Mod.*;

@EventBusSubscriber
public class ServerConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final List<FarmlandConfig> FARMLAND_CONFIG = new ArrayList<>();

    public static final ModConfigSpec.DoubleValue LUCK_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CROP_TARGET_CHANCE;
    public static final ModConfigSpec.DoubleValue SEED_CHANCE_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue HANDLE_COMPACTING;
    public static final ModConfigSpec.BooleanValue HANDLE_SEED_RECIPES;
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
        HANDLE_SEED_RECIPES = BUILDER.comment("Attempt to handle recipes involving seed items automatically (to avoid having to add all of them to the retain_quality_recipes config)").define("handle_seed_recipes", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void reloadConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC && /* Cannot be the case when stopping the server? */ SPEC.isLoaded()) {
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

    public static boolean isNoQualityRecipe(@Nullable final RecipeHolder<?> recipe, final RegistryAccess access) {
        if (recipe == null) {
            return false;
        }

        RecipeExtension.QualityFoodStatus status = getRecipeStatus(recipe, access);
        return status == RecipeExtension.QualityFoodStatus.NO_QUALITY || status == RecipeExtension.QualityFoodStatus.NO_QUALITY_AND_RETAIN_QUALITY;
    }

    public static boolean isRetainQualityRecipe(@Nullable final RecipeHolder<?> recipe, @Nullable RegistryAccess access) {
        if (recipe == null) {
            return false;
        }

        if (ServerConfig.HANDLE_SEED_RECIPES.get()) {
            if (access == null && ServerLifecycleHooks.getCurrentServer() != null) {
                access = ServerLifecycleHooks.getCurrentServer().registryAccess();
            }
        }

        RecipeExtension.QualityFoodStatus status = getRecipeStatus(recipe, access);
        return status == RecipeExtension.QualityFoodStatus.RETAIN_QUALITY || status == RecipeExtension.QualityFoodStatus.NO_QUALITY_AND_RETAIN_QUALITY;
    }

    private static RecipeExtension.QualityFoodStatus getRecipeStatus(@NotNull final RecipeHolder<?> recipe, @Nullable final RegistryAccess access) {
        RecipeExtension extension = (RecipeExtension) (Object) recipe;

        if (extension.quality_food$getStatus() == RecipeExtension.QualityFoodStatus.NOT_INITIALIZED) {
            boolean isNoQualityRecipe = NO_QUALITY_RECIPES.get().contains(recipe.id().toString());
            boolean isRetainQualityRecipe = RETAIN_QUALITY_RECIPES.get().contains(recipe.id().toString());

            if (!isRetainQualityRecipe && access != null) {
                isRetainQualityRecipe = recipe.value().getResultItem(access).is(Tags.Items.SEEDS);
            }

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

        return 1;
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

        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/apple_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/beetroot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/beetroot_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/berry_sack").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/berry_sack_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/carrot_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/chorus_fruit_block").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/chorus_fruit_block_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/cocoa_bean_sack").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/cocoa_bean_sack_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/glowberry_sack").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/glowberry_sack_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/golden_apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/golden_apple_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/golden_carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/golden_carrot_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/potato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/potato_crate_uncompress").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/sugar_cane_block").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(QUARK.modid(), "building/crafting/compressed/sugar_cane_block_uncompress").toString());

        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "carrot_from_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "potato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "potato_from_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "beetroot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "beetroot_from_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "cabbage_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "cabbage").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "tomato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "tomato").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "onion_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "onion").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "rice_bale").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "rice_panicle").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "rice_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "rice_from_bag").toString());

        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY.modid(), "white_grape_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY.modid(), "white_grape").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY.modid(), "red_grape_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY.modid(), "red_grape").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY.modid(), "cherry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY.modid(), "cherries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY.modid(), "apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(VINERY.modid(), "apples").toString());

        NO_QUALITY_RECIPES_DEFAULT.add(location(SUPPLEMENTARIES.modid(), "sugar_cube").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(SUPPLEMENTARIES.modid(), "sugar_cube_uncrafting").toString());

        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "apples").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "beetroot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "beetroots").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "berry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "berries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "brown_mushroom_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "brown_mushroom").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "carrots").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "cocoabeans_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "cocoabeans").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "cod_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "cod").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "egg_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "eggs").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "glowberry_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "glowberries").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "golden_apple_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "golden_apple").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "golden_carrot_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "golden_carrot").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "potato_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "potatoes").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "red_mushroom_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "red_mushroom").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "salmon_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "salmon").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "stacked_melons").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "melons").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "stacked_pumpkins").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "pumpkins").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "sugar_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "sugar").toString());
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
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "stacked_melons").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "stacked_pumpkins").toString());
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

        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "lettuce_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "lettuce").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "tomato_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "tomato").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "carrot_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "carrot_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "potato_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "potato_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "onion_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "onion_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "beetroot_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "beetroot_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "corn_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "corn_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "strawberry_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "strawberry_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "flour_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "flour_from_bag").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "oat_ball").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "oat_from_ball").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "barley_ball").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(FARM_AND_CHARM.modid(), "barley_from_ball").toString());

//        NO_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "dried_green_tea_leaf_block").toString());
//        NO_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "green_tea_leaf_block").toString());
//        NO_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "mixed_tea_leaf_block").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "tea_leaf_crate").toString());
        NO_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "tea_leafs_from_crate").toString());
    }

    private static void fillRetainQualityRecipes() {
        // Glass bottle in recipe
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_sugar_cane");
        RETAIN_QUALITY_RECIPES_DEFAULT.add("minecraft:sugar_from_honey_bottle");

        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "stacked_melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "stacked_pumpkins").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(CRATE_DELIGHT.modid(), "pumpkins").toString());
        // Still Crate Delight
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "stacked_melons").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(FARMERSDELIGHT.modid(), "stacked_pumpkins").toString());

        // Other

        // 2x3 storage block
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "tea_leaf_crate").toString());
        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "tea_leafs_from_crate").toString());
        // Mixed input items
//        RETAIN_QUALITY_RECIPES_DEFAULT.add(location(HERALBREWS.modid(), "mixed_tea_leaf_block").toString());
    }
}
