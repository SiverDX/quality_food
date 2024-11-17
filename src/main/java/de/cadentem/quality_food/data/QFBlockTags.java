package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.compat.Compat;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static de.cadentem.quality_food.compat.Compat.*;

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
                .addOptionalTag(location(FARMERSDELIGHT, "wild_crops"))
                .addOptionalTag(location(FARM_AND_CHARM, "wild_crops"))
                .addOptional(location(FARMERSDELIGHT, "rice"))
                .addOptional(location(COLLECTORS_REAP, "lime_cake"))
                .addOptional(location(COLLECTORS_REAP, "pomegranate_cake"))
                /* Storage blocks */
                .addOptional(location(FARMERSDELIGHT, "carrot_crate"))
                .addOptional(location(FARMERSDELIGHT, "potato_crate"))
                .addOptional(location(FARMERSDELIGHT, "beetroot_crate"))
                .addOptional(location(FARMERSDELIGHT, "cabbage_crate"))
                .addOptional(location(FARMERSDELIGHT, "tomato_crate"))
                .addOptional(location(FARMERSDELIGHT, "onion_crate"))
                .addOptional(location(FARMERSDELIGHT, "chorus_crate"))
                .addOptional(location(FARMERSDELIGHT, "rice_bale"))
                .addOptional(location(FARMERSDELIGHT, "rice_bag"))

                .addOptional(location(QUARK, "apple_crate"))
                .addOptional(location(QUARK, "beetroot_crate"))
                .addOptional(location(QUARK, "berry_sack"))
                .addOptional(location(QUARK, "carrot_crate"))
                .addOptional(location(QUARK, "chorus_fruit_block"))
                .addOptional(location(QUARK, "cocoa_beans_sack"))
                .addOptional(location(QUARK, "glowberry_sack"))
                .addOptional(location(QUARK, "golden_apple_crate"))
                .addOptional(location(QUARK, "golden_carrot_crate"))
                .addOptional(location(QUARK, "potato_crate"))
                .addOptional(location(QUARK, "sugar_cane_block"))

                .addOptional(location(SUPPLEMENTARIES, "sugar_cube"))

                .addOptional(location(VINERY, "white_grape_bag"))
                .addOptional(location(VINERY, "red_grape_bag"))
                .addOptional(location(VINERY, "cherry_bag"))
                .addOptional(location(VINERY, "apple_bag"))

                .addOptional(location(CRATE_DELIGHT, "apple_crate"))
                .addOptional(location(CRATE_DELIGHT, "banana_crate"))
                .addOptional(location(CRATE_DELIGHT, "bass_crate"))
                .addOptional(location(CRATE_DELIGHT, "beetroot_crate"))
                .addOptional(location(CRATE_DELIGHT, "berry_crate"))
                .addOptional(location(CRATE_DELIGHT, "blueberry_crate"))
                .addOptional(location(CRATE_DELIGHT, "brown_mushroom_crate"))
                .addOptional(location(CRATE_DELIGHT, "caiman_egg_crate"))
                .addOptional(location(CRATE_DELIGHT, "carrot_crate"))
                .addOptional(location(CRATE_DELIGHT, "catfish_crate"))
                .addOptional(location(CRATE_DELIGHT, "cocoabeans_bag"))
                .addOptional(location(CRATE_DELIGHT, "cod_crate"))
                .addOptional(location(CRATE_DELIGHT, "cookie_bag"))
                .addOptional(location(CRATE_DELIGHT, "crocodile_egg_crate"))
                .addOptional(location(CRATE_DELIGHT, "diamond_apple_crate"))
                .addOptional(location(CRATE_DELIGHT, "duck_egg_crate"))
                .addOptional(location(CRATE_DELIGHT, "egg_crate"))
                .addOptional(location(CRATE_DELIGHT, "emu_egg_crate"))
                .addOptional(location(CRATE_DELIGHT, "end_fish_crate"))
                .addOptional(location(CRATE_DELIGHT, "glowberry_crate"))
                .addOptional(location(CRATE_DELIGHT, "golden_apple_crate"))
                .addOptional(location(CRATE_DELIGHT, "golden_carrot_crate"))
                .addOptional(location(CRATE_DELIGHT, "kiwi_egg_crate"))
                .addOptional(location(CRATE_DELIGHT, "kiwifruit_crate"))
                .addOptional(location(CRATE_DELIGHT, "platypus_egg_crate"))
                .addOptional(location(CRATE_DELIGHT, "potato_crate"))
                .addOptional(location(CRATE_DELIGHT, "red_mushroom_crate"))
                .addOptional(location(CRATE_DELIGHT, "salmon_crate"))
                .addOptional(location(CRATE_DELIGHT, "stacked_melons"))
                .addOptional(location(CRATE_DELIGHT, "stacked_pumpkins"))
                .addOptional(location(CRATE_DELIGHT, "sugar_bag"))
                .addOptional(location(CRATE_DELIGHT, "terrapin_egg_crate"))
                .addOptional(location(CRATE_DELIGHT, "wheat_flour_bag"))

                .addOptional(location(FARM_AND_CHARM, "lettuce_bag"))
                .addOptional(location(FARM_AND_CHARM, "tomato_bag"))
                .addOptional(location(FARM_AND_CHARM, "carrot_bag"))
                .addOptional(location(FARM_AND_CHARM, "potato_bag"))
                .addOptional(location(FARM_AND_CHARM, "onion_bag"))
                .addOptional(location(FARM_AND_CHARM, "beetroot_bag"))
                .addOptional(location(FARM_AND_CHARM, "corn_bag"))
                .addOptional(location(FARM_AND_CHARM, "strawberry_bag"))
                .addOptional(location(FARM_AND_CHARM, "flour_bag"))
                .addOptional(location(FARM_AND_CHARM, "oat_bale"))
                .addOptional(location(FARM_AND_CHARM, "barley_ball"))

                .addOptional(location("miners_delight", "cave_carrot_crate"))
        ;
    }
}
