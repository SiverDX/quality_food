package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
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
        ;
    }
}
