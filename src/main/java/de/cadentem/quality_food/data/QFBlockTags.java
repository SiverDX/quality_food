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
                .addOptionalTag(Compat.farmersdelight("wild_crops"))
                .addOptional(Compat.farmersdelight("rice"))
                /* Storage blocks */
                .addOptional(Compat.farmersdelight("carrot_crate"))
                .addOptional(Compat.farmersdelight("potato_crate"))
                .addOptional(Compat.farmersdelight("beetroot_crate"))
                .addOptional(Compat.farmersdelight("cabbage_crate"))
                .addOptional(Compat.farmersdelight("tomato_crate"))
                .addOptional(Compat.farmersdelight("onion_crate"))
                .addOptional(Compat.farmersdelight("chorus_crate"))
                .addOptional(Compat.farmersdelight("rice_bale"))
                .addOptional(Compat.farmersdelight("rice_bag"))
                .addOptional(Compat.quark("apple_crate"))
                .addOptional(Compat.quark("beetroot_crate"))
                .addOptional(Compat.quark("berry_sack"))
                .addOptional(Compat.quark("carrot_crate"))
                .addOptional(Compat.quark("chorus_fruit_block"))
                .addOptional(Compat.quark("cocoa_beans_sack"))
                .addOptional(Compat.quark("glowberry_sack"))
                .addOptional(Compat.quark("golden_apple_crate"))
                .addOptional(Compat.quark("golden_carrot_crate"))
                .addOptional(Compat.quark("potato_crate"))
                .addOptional(Compat.quark("sugar_cane_block"))
                .addOptional(Compat.supplementaries("sugar_cube"))
                .addOptional(Compat.vinery("white_grape_bag"))
                .addOptional(Compat.vinery("red_grape_bag"))
                .addOptional(Compat.vinery("cherry_bag"))
                .addOptional(Compat.vinery("apple_bag"));
        ;
    }
}
