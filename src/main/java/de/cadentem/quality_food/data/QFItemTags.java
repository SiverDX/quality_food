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
                .addOptionalTag(Compat.farmersdelight("wild_crops"))
                .addOptional(Compat.farmersdelight("rice_panicle"))
                .addOptional(Compat.fruitfulfun("lemon_roast_chicken_block"))
                .addOptional(Compat.collectorsreap("pomegranate"))
                .addOptional(Compat.farmandcharm("barley"))
                .addOptional(Compat.farmandcharm("oat"))
        ;

        tag(BLACKLIST)
                .addOptional(Compat.supplementaries("flax"))
                .addOptional(Compat.supplementaries("flax_seeds"));
    }
}