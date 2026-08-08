package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.compat.Compat;
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
                .addOptionalTag(Compat.forge("dough")) // Farmer's Delight
                .addOptionalTag(Compat.forge("flour")) // Farmer's Delight
                .addOptionalTag(Compat.location(Compat.Mod.FARMERSDELIGHT.modid(), "wild_crops"))
                .addOptionalTag(Compat.location(Compat.Mod.FARMERSDELIGHT.modid(), "pies"))
                .addOptional(Compat.location(Compat.Mod.FARMERSDELIGHT.modid(), "rice_panicle"))
                .addOptional(Compat.location(Compat.Mod.FARMERSDELIGHT.modid(), "debug_pumpkin_pie"))
                .addOptional(Compat.location(Compat.Mod.FRUITFUL_FUN.modid(), "lemon_roast_chicken_block"))
                .addOptional(Compat.location(Compat.Mod.COLLECTORS_REAP.modid(), "pomegranate"))
                .addOptional(Compat.location(Compat.Mod.FARM_AND_CHARM.modid(), "barley"))
                .addOptional(Compat.location(Compat.Mod.FARM_AND_CHARM.modid(), "oat"))
                .addOptional(Compat.location(Compat.Mod.HERALBREWS.modid(), "hibiscus"))
                .addOptional(Compat.location(Compat.Mod.HERALBREWS.modid(), "lavender_blossom"))
                .addOptional(Compat.location(Compat.Mod.HERALBREWS.modid(), "green_tea_leaf"))
                .addOptional(Compat.location(Compat.Mod.VINTAGEDELIGHT.modid(), "cheese_wheel"));
        ;

        tag(BLACKLIST)
                .addOptional(Compat.location(Compat.Mod.SUPPLEMENTARIES.modid(), "flax"))
                .addOptional(Compat.location(Compat.Mod.SUPPLEMENTARIES.modid(), "flax_seeds"));
    }
}