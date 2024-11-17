package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static de.cadentem.quality_food.compat.Compat.*;

public class QFItemTags extends ItemTagsProvider {
    public static final TagKey<Item> MATERIAL_WHITELIST = TagKey.create(Registries.ITEM, new ResourceLocation(QualityFood.MODID, "material_whitelist"));
    public static final TagKey<Item> BLACKLIST = TagKey.create(Registries.ITEM, new ResourceLocation(QualityFood.MODID, "blacklist"));

    public QFItemTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider, final CompletableFuture<TagLookup<Block>> blockTags, @Nullable final ExistingFileHelper helper) {
        super(output, provider, blockTags, QualityFood.MODID, helper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(MATERIAL_WHITELIST)
                .add(Items.SUGAR)
                .add(Items.SUGAR_CANE)
                .add(Items.COCOA_BEANS)
                .add(Items.INK_SAC) // Farmer's Delight
                .add(Items.HAY_BLOCK)
                .add(Items.HONEY_BLOCK)
                .addTag(Tags.Items.EGGS)
                .addTag(Tags.Items.SEEDS)
                .addTag(Tags.Items.CROPS)
                .addTag(Tags.Items.MUSHROOMS)
                .addOptionalTag(forge("dough")) // Farmer's Delight
                .addOptionalTag(forge("flour")) // Farmer's Delight
                .addOptionalTag(location(FARMERSDELIGHT, "wild_crops"))
                .addOptional(location(FARMERSDELIGHT, "rice_panicle"))
                .addOptional(location(FRUITFUL_FUN, "lemon_roast_chicken_block"))
                .addOptional(location(COLLECTORS_REAP, "pomegranate"))
                .addOptional(location(FARM_AND_CHARM, "barley"))
                .addOptional(location(FARM_AND_CHARM, "oat"))
        ;

        tag(BLACKLIST)
                .addOptional(location(SUPPLEMENTARIES, "flax"))
                .addOptional(location(SUPPLEMENTARIES, "flax_seeds"));
    }
}