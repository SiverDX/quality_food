package de.cadentem.quality_food.data;

import net.minecraft.tags.TagEntry;
import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static de.cadentem.quality_food.compat.Compat.Mod.COLLECTORS_REAP;
import static de.cadentem.quality_food.compat.Compat.Mod.CRATE_DELIGHT;
import static de.cadentem.quality_food.compat.Compat.Mod.FARMERSDELIGHT;
import static de.cadentem.quality_food.compat.Compat.Mod.FARM_AND_CHARM;
import static de.cadentem.quality_food.compat.Compat.Mod.HERALBREWS;
import static de.cadentem.quality_food.compat.Compat.Mod.QUARK;
import static de.cadentem.quality_food.compat.Compat.Mod.SUPPLEMENTARIES;
import static de.cadentem.quality_food.compat.Compat.Mod.VINERY;
import static de.cadentem.quality_food.compat.Compat.Mod.VINTAGEDELIGHT;
import static de.cadentem.quality_food.compat.Compat.location;

public class QFBlockTags extends BlockTagsProvider {
    public static final TagKey<Block> QUALITY_BLOCKS = BlockTags.create(QualityFood.location("quality_blocks"));

    public QFBlockTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, QualityFood.MODID);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(QUALITY_BLOCKS)
                .addTag(BlockTags.CROPS)
                .addTag(BlockTags.CANDLE_CAKES)
                .add(Blocks.PUMPKIN)
                .add(Blocks.PUMPKIN_STEM)
                .add(Blocks.ATTACHED_PUMPKIN_STEM)
                .add(Blocks.MELON)
                .add(Blocks.MELON_STEM)
                .add(Blocks.ATTACHED_MELON_STEM)
                .add(Blocks.SWEET_BERRY_BUSH)
                .add(Blocks.CAVE_VINES)
                .add(Blocks.CAVE_VINES_PLANT)
                .add(Blocks.COCOA)
                .add(Blocks.SUGAR_CANE)
                .add(Blocks.CAKE)
                .add(Blocks.HAY_BLOCK)
                .add(Blocks.HONEY_BLOCK)
                .add(Blocks.HONEYCOMB_BLOCK)
                .remove(Blocks.TORCHFLOWER)
                .remove(Blocks.PITCHER_CROP)
                .addOptionalTag(TagKey.create(Registries.BLOCK, location(FARMERSDELIGHT.modid(), "wild_crops")))
                .addOptionalTag(TagKey.create(Registries.BLOCK, location(FARM_AND_CHARM.modid(), "wild_crops")))
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "rice")))
                .add(TagEntry.optionalElement(location(COLLECTORS_REAP.modid(), "lime_cake")))
                .add(TagEntry.optionalElement(location(COLLECTORS_REAP.modid(), "pomegranate_cake")))
                .add(TagEntry.optionalElement(location(HERALBREWS.modid(), "dried_green_tea_leaf_block")))
                .add(TagEntry.optionalElement(location(HERALBREWS.modid(), "dried_out_green_tea_leaf_block")))
                .add(TagEntry.optionalElement(location(HERALBREWS.modid(), "mixed_tea_leaf_block")))
                .add(TagEntry.optionalElement(location(HERALBREWS.modid(), "oolong_tea_leaf_block")))
                .add(TagEntry.optionalElement(location(HERALBREWS.modid(), "green_tea_leaf_block")))
                .add(TagEntry.optionalElement(location(HERALBREWS.modid(), "black_tea_leaf_block")))
                /* Storage blocks */
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "carrot_crate")))
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "potato_crate")))
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "beetroot_crate")))
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "cabbage_crate")))
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "tomato_crate")))
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "onion_crate")))
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "chorus_crate")))
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "rice_bale")))
                .add(TagEntry.optionalElement(location(FARMERSDELIGHT.modid(), "rice_bag")))

                .add(TagEntry.optionalElement(location(QUARK.modid(), "apple_crate")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "beetroot_crate")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "berry_sack")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "carrot_crate")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "chorus_fruit_block")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "cocoa_beans_sack")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "glowberry_sack")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "golden_apple_crate")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "golden_carrot_crate")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "potato_crate")))
                .add(TagEntry.optionalElement(location(QUARK.modid(), "sugar_cane_block")))

                .add(TagEntry.optionalElement(location(SUPPLEMENTARIES.modid(), "sugar_cube")))

                .add(TagEntry.optionalElement(location(VINERY.modid(), "white_grape_bag")))
                .add(TagEntry.optionalElement(location(VINERY.modid(), "red_grape_bag")))
                .add(TagEntry.optionalElement(location(VINERY.modid(), "cherry_bag")))
                .add(TagEntry.optionalElement(location(VINERY.modid(), "apple_bag")))

                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "apple_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "banana_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "bass_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "beetroot_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "berry_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "blueberry_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "brown_mushroom_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "caiman_egg_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "carrot_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "catfish_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "cocoabeans_bag")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "cod_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "cookie_bag")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "crocodile_egg_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "diamond_apple_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "duck_egg_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "egg_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "emu_egg_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "end_fish_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "glowberry_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "golden_apple_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "golden_carrot_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "kiwi_egg_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "kiwifruit_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "platypus_egg_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "potato_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "red_mushroom_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "salmon_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "stacked_melons")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "stacked_pumpkins")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "sugar_bag")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "terrapin_egg_crate")))
                .add(TagEntry.optionalElement(location(CRATE_DELIGHT.modid(), "wheat_flour_bag")))

                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "lettuce_bag")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "tomato_bag")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "carrot_bag")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "potato_bag")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "onion_bag")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "beetroot_bag")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "corn_bag")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "strawberry_bag")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "flour_bag")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "oat_bale")))
                .add(TagEntry.optionalElement(location(FARM_AND_CHARM.modid(), "barley_ball")))

                .add(TagEntry.optionalElement(location("miners_delight", "cave_carrot_crate")))

                .add(TagEntry.optionalElement(location(HERALBREWS.modid(), "tea_leaf_crate")))

                .add(TagEntry.optionalElement(location(VINTAGEDELIGHT.modid(), "oat_bag")))
                .add(TagEntry.optionalElement(location(VINTAGEDELIGHT.modid(), "oat_bale")))
                .add(TagEntry.optionalElement(location(VINTAGEDELIGHT.modid(), "ghost_pepper_crate")))
                .add(TagEntry.optionalElement(location(VINTAGEDELIGHT.modid(), "cucumber_crate")))
                .add(TagEntry.optionalElement(location(VINTAGEDELIGHT.modid(), "peanut_crate")))
                .add(TagEntry.optionalElement(location(VINTAGEDELIGHT.modid(), "gearo_berry_bag")))

                .add(TagEntry.optionalElement(location("hearthandharvest", "blueberry_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "cherry_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "red_grape_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "green_grape_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "raspberry_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "peanut_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "corn_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "apple_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "golden_apple_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "golden_carrot_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "glistering_melon_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "poisonous_potato_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "rotten_tomato_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "glow_berry_crate")))
                .add(TagEntry.optionalElement(location("hearthandharvest", "sweet_berry_crate")))
        ;
    }
}
