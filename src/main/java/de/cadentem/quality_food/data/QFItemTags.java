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
                .addOptionalTag(forge("dough"))
                .addOptionalTag(forge("flour"))
                .addOptionalTag(farmersdelight("wild_crops"))
                .add(Items.SUGAR)
                .add(Items.SUGAR_CANE)
                .addOptional(farmersdelight("rice_panicle"))
                /* Storage blocks */
                .add(Items.HAY_BLOCK)
                .add(Items.HONEY_BLOCK)
                .addOptional(farmersdelight("carrot_crate"))
                .addOptional(farmersdelight("potato_crate"))
                .addOptional(farmersdelight("beetroot_crate"))
                .addOptional(farmersdelight("cabbage_crate"))
                .addOptional(farmersdelight("tomato_crate"))
                .addOptional(farmersdelight("onion_crate"))
                .addOptional(farmersdelight("chorus_crate"))
                .addOptional(farmersdelight("rice_bale"))
                .addOptional(farmersdelight("rice_bag"))
                .addOptional(quark("golden_apple_crate"))
                .addOptional(quark("apple_crate"))
                .addOptional(quark("potato_crate"))
                .addOptional(quark("carrot_crate"))
                .addOptional(quark("golden_carrot_crate"))
                .addOptional(quark("beetroot_crate"))
                .addOptional(quark("sugar_cane_block"))
                .addOptional(quark("cocoa_beans_sack"))
                .addOptional(quark("nether_wart_stack"))
                .addOptional(quark("berry_sack"))
                .addOptional(quark("glowberry_sack"))
                .addOptional(supplementaries("sugar_cube"))
                .addOptional(vinery("white_grape_crate"))
                .addOptional(vinery("red_grape_crate"))
                .addOptional(vinery("cherry_crate"))
                .addOptional(vinery("apple_crate"));

        tag(RECIPE_CONVERSION)
                .addTag(Tags.Items.SEEDS)
                .add(Items.SUGAR)
                .add(Items.HONEY_BOTTLE)
                .addOptional(farmersdelight("rice"));

        tag(BLACKLIST)
                .addTag(ItemTags.FLOWERS)
                .addTag(ItemTags.SAPLINGS)
                .add(Items.GRASS)
                .add(Items.TALL_GRASS)
                .add(Items.DEAD_BUSH)
                .addOptional(supplementaries("flax"))
                .addOptional(supplementaries("flax_seeds"));
    }

    private ResourceLocation quark(final String path) {
        return location("quark", path);
    }

    private ResourceLocation farmersdelight(final String path) {
        return location("farmersdelight", path);
    }

    private ResourceLocation supplementaries(final String path) {
        return location("supplementaries", path);
    }

    private ResourceLocation vinery(final String path) {
        return location("vinery", path);
    }

    private ResourceLocation forge(final String path) {
        return location("forge", path);
    }

    private ResourceLocation location(final String namespace, final String path) {
        return new ResourceLocation(namespace, path);
    }
}