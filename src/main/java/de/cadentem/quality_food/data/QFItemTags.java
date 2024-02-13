package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class QFItemTags extends ItemTagsProvider {
    public static final TagKey<Item> MATERIAL_WHITELIST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(QualityFood.MODID, "material_whitelist"));

    public QFItemTags(final DataGenerator generator, final BlockTagsProvider provider, final ExistingFileHelper fileHelper) {
        super(generator, provider, QualityFood.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        tag(MATERIAL_WHITELIST)
                .addTag(Tags.Items.EGGS)
                .addTag(Tags.Items.CROPS)
                .addOptionalTag(new ResourceLocation("farmersdelight", "wild_crops"))
                .addOptionalTag(new ResourceLocation("forge", "dough"))
                .add(Items.SUGAR);
    }
}