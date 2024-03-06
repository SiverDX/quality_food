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
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class QFItemTags extends ItemTagsProvider {
    public static final TagKey<Item> MATERIAL_WHITELIST = TagKey.create(Registries.ITEM, new ResourceLocation(QualityFood.MODID, "material_whitelist"));
    public static final TagKey<Item> RECIPE_CONVERSION = TagKey.create(Registries.ITEM, new ResourceLocation(QualityFood.MODID, "recipe_conversion"));
    public static final TagKey<Item> BLACKLIST = TagKey.create(Registries.ITEM, new ResourceLocation(QualityFood.MODID, "blacklist"));

    public QFItemTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider, @Nullable final ExistingFileHelper fileHelper) {
        super(output, provider, CompletableFuture.completedFuture(null), QualityFood.MODID, fileHelper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(MATERIAL_WHITELIST)
                .add(Items.SUGAR)
                .addTag(Tags.Items.EGGS)
                .addTag(Tags.Items.SEEDS)
                .addTag(Tags.Items.CROPS)
                .addTag(Tags.Items.MUSHROOMS)
                .addOptionalTag(new ResourceLocation("forge", "dough"))
                .addOptionalTag(new ResourceLocation("farmersdelight", "wild_crops"))
                .addOptional(new ResourceLocation("farmersdelight", "rice_panicle"));

        tag(RECIPE_CONVERSION)
                .addTag(Tags.Items.SEEDS)
                .addOptional(new ResourceLocation("farmersdelight", "rice"));

        tag(BLACKLIST)
                .addTag(ItemTags.FLOWERS)
                .addTag(ItemTags.SAPLINGS)
                .add(Items.GRASS)
                .add(Items.TALL_GRASS)
                .add(Items.DEAD_BUSH);
    }
}