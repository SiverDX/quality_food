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
    // public static final TagKey<Item> HARVESTABLE = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(QualityFood.MODID, "harvestable"));

    public QFItemTags(final DataGenerator generator, final BlockTagsProvider provider, final ExistingFileHelper fileHelper) {
        super(generator, provider, QualityFood.MODID, fileHelper);
    }

    @Override
    protected void addTags() {
        tag(MATERIAL_WHITELIST)
                .addTag(Tags.Items.EGGS)
                .addTag(Tags.Items.CROPS)
                .addTag(Tags.Items.MUSHROOMS)
                .addOptionalTag(new ResourceLocation("forge", "dough"))
                .addOptionalTag(new ResourceLocation("farmersdelight", "wild_crops"))
                .add(Items.SUGAR);

        /*
        tag(HARVESTABLE)
                .add(Items.GLOW_BERRIES)
                .addOptional(new ResourceLocation("vinery", "red_grape"))
                .addOptional(new ResourceLocation("vinery", "white_grape"))
                .addOptional(new ResourceLocation("vinery", "savanna_grapes_red"))
                .addOptional(new ResourceLocation("vinery", "savanna_grapes_white"))
                .addOptional(new ResourceLocation("vinery", "taiga_grapes_red"))
                .addOptional(new ResourceLocation("vinery", "taiga_grapes_white"))
                .addOptional(new ResourceLocation("vinery", "jungle_grapes_red"))
                .addOptional(new ResourceLocation("vinery", "jungle_grapes_white"))
                .addOptional(new ResourceLocation("vinery", "cherry"))
                .addOptional(new ResourceLocation("vinery", "rotten_cherry"))
                .addOptional(new ResourceLocation("vinery", "rotten_cherry"))
        ;
        */
    }
}