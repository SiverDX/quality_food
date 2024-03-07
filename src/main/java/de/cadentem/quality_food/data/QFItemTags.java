package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class QFItemTags extends ItemTagsProvider {
    public static final TagKey<Item> MATERIAL_WHITELIST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(QualityFood.MODID, "material_whitelist"));
    public static final TagKey<Item> RECIPE_CONVERSION = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(QualityFood.MODID, "recipe_conversion"));
    public static final TagKey<Item> BLACKLIST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(QualityFood.MODID, "blacklist"));

    public QFItemTags(final DataGenerator generator, final BlockTagsProvider provider, final ExistingFileHelper fileHelper) {
        super(generator, provider, QualityFood.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        tag(MATERIAL_WHITELIST)
                .addTag(Tags.Items.EGGS)
                .addTag(Tags.Items.SEEDS)
                .addTag(Tags.Items.CROPS)
                .addTag(Tags.Items.MUSHROOMS)
                .addOptionalTag(new ResourceLocation("forge", "dough"))
                .addOptionalTag(new ResourceLocation("farmersdelight", "wild_crops"))
                .add(Items.SUGAR)
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