package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static de.cadentem.quality_food.compat.Compat.*;
import static de.cadentem.quality_food.compat.Compat.Mod.*;

public class QFBlockTags extends BlockTagsProvider {
    public static final TagKey<Block> QUALITY_BLOCKS = BlockTags.create(QualityFood.location("quality_blocks"));

    public QFBlockTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider, @Nullable final ExistingFileHelper helper) {
        super(output, provider, QualityFood.MODID, helper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(QUALITY_BLOCKS)
                .addTag(BlockTags.CROPS)
                .addTag(BlockTags.CANDLE_CAKES)
                .add(Blocks.PUMPKIN)
                .add(Blocks.MELON)
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
                .addOptionalTag(location(FARMERSDELIGHT.modid(), "wild_crops"))
                .addOptionalTag(location(FARM_AND_CHARM.modid(), "wild_crops"))
                .addOptional(location(FARMERSDELIGHT.modid(), "rice"))
                .addOptional(location(COLLECTORS_REAP.modid(), "lime_cake"))
                .addOptional(location(COLLECTORS_REAP.modid(), "pomegranate_cake"))
                /* Storage blocks */
                .addOptional(location(FARMERSDELIGHT.modid(), "carrot_crate"))
                .addOptional(location(FARMERSDELIGHT.modid(), "potato_crate"))
                .addOptional(location(FARMERSDELIGHT.modid(), "beetroot_crate"))
                .addOptional(location(FARMERSDELIGHT.modid(), "cabbage_crate"))
                .addOptional(location(FARMERSDELIGHT.modid(), "tomato_crate"))
                .addOptional(location(FARMERSDELIGHT.modid(), "onion_crate"))
                .addOptional(location(FARMERSDELIGHT.modid(), "chorus_crate"))
                .addOptional(location(FARMERSDELIGHT.modid(), "rice_bale"))
                .addOptional(location(FARMERSDELIGHT.modid(), "rice_bag"))

                .addOptional(location(QUARK.modid(), "apple_crate"))
                .addOptional(location(QUARK.modid(), "beetroot_crate"))
                .addOptional(location(QUARK.modid(), "berry_sack"))
                .addOptional(location(QUARK.modid(), "carrot_crate"))
                .addOptional(location(QUARK.modid(), "chorus_fruit_block"))
                .addOptional(location(QUARK.modid(), "cocoa_beans_sack"))
                .addOptional(location(QUARK.modid(), "glowberry_sack"))
                .addOptional(location(QUARK.modid(), "golden_apple_crate"))
                .addOptional(location(QUARK.modid(), "golden_carrot_crate"))
                .addOptional(location(QUARK.modid(), "potato_crate"))
                .addOptional(location(QUARK.modid(), "sugar_cane_block"))

                .addOptional(location(SUPPLEMENTARIES.modid(), "sugar_cube"))

                .addOptional(location(VINERY.modid(), "white_grape_bag"))
                .addOptional(location(VINERY.modid(), "red_grape_bag"))
                .addOptional(location(VINERY.modid(), "cherry_bag"))
                .addOptional(location(VINERY.modid(), "apple_bag"))

                .addOptional(location(CRATE_DELIGHT.modid(), "apple_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "banana_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "bass_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "beetroot_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "berry_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "blueberry_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "brown_mushroom_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "caiman_egg_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "carrot_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "catfish_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "cocoabeans_bag"))
                .addOptional(location(CRATE_DELIGHT.modid(), "cod_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "cookie_bag"))
                .addOptional(location(CRATE_DELIGHT.modid(), "crocodile_egg_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "diamond_apple_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "duck_egg_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "egg_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "emu_egg_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "end_fish_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "glowberry_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "golden_apple_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "golden_carrot_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "kiwi_egg_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "kiwifruit_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "platypus_egg_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "potato_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "red_mushroom_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "salmon_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "stacked_melons"))
                .addOptional(location(CRATE_DELIGHT.modid(), "stacked_pumpkins"))
                .addOptional(location(CRATE_DELIGHT.modid(), "sugar_bag"))
                .addOptional(location(CRATE_DELIGHT.modid(), "terrapin_egg_crate"))
                .addOptional(location(CRATE_DELIGHT.modid(), "wheat_flour_bag"))

                .addOptional(location(FARM_AND_CHARM.modid(), "lettuce_bag"))
                .addOptional(location(FARM_AND_CHARM.modid(), "tomato_bag"))
                .addOptional(location(FARM_AND_CHARM.modid(), "carrot_bag"))
                .addOptional(location(FARM_AND_CHARM.modid(), "potato_bag"))
                .addOptional(location(FARM_AND_CHARM.modid(), "onion_bag"))
                .addOptional(location(FARM_AND_CHARM.modid(), "beetroot_bag"))
                .addOptional(location(FARM_AND_CHARM.modid(), "corn_bag"))
                .addOptional(location(FARM_AND_CHARM.modid(), "strawberry_bag"))
                .addOptional(location(FARM_AND_CHARM.modid(), "flour_bag"))
                .addOptional(location(FARM_AND_CHARM.modid(), "oat_bale"))
                .addOptional(location(FARM_AND_CHARM.modid(), "barley_ball"))

                .addOptional(location("miners_delight", "cave_carrot_crate"))

                .addOptional(location(HERALBREWS.modid(), "tea_leaf_crate"))
//                .addOptional(location(HERALBREWS.modid(), "green_tea_leaf_block"))
//                .addOptional(location(HERALBREWS.modid(), "dried_green_tea_leaf_block"))
//                .addOptional(location(HERALBREWS.modid(), "black_tea_leaf_block"))
//                .addOptional(location(HERALBREWS.modid(), "mixed_tea_leaf_block"))
//                .addOptional(location(HERALBREWS.modid(), "oolong_leaf_block"))

        ;
    }
}