package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.compat.Compat;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static de.cadentem.quality_food.compat.Compat.*;

public class QFItemTags extends ItemTagsProvider {
    public static final TagKey<Item> MATERIAL_WHITELIST = ItemTags.create(QualityFood.location("material_whitelist"));
    public static final TagKey<Item> BLACKLIST = ItemTags.create(QualityFood.location("blacklist"));

    public QFItemTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider, final CompletableFuture<TagLookup<Block>> blockTags, @Nullable final ExistingFileHelper helper) {
        super(output, provider, blockTags, QualityFood.MODID, helper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(MATERIAL_WHITELIST)
                .addTag(Tags.Items.MUSHROOMS)
                .addTag(Tags.Items.FOODS)
                .addTag(Tags.Items.CROPS)
                .addTag(Tags.Items.SEEDS)
                .addTag(Tags.Items.EGGS)
                .add(Items.COCOA_BEANS)
                .add(Items.SUGAR_CANE)
                .add(Items.SUGAR)
                .add(Items.INK_SAC) // Farmer's Delight
                .add(Items.HAY_BLOCK)
                .add(Items.HONEY_BLOCK)
                .addOptionalTag(location(FARMERSDELIGHT, "wild_crops"))
                .addOptional(location(FARMERSDELIGHT, "rice_panicle"))
                .addOptional(location(FARM_AND_CHARM, "barley"))
                .addOptional(location(FARM_AND_CHARM, "oat"));

        tag(BLACKLIST)
                .addOptional(location(SUPPLEMENTARIES, "flax"))
                .addOptional(location(SUPPLEMENTARIES, "flax_seeds"));
    }
}