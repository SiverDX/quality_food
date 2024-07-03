package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import de.cadentem.quality_food.compat.Compat;
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
    public static final TagKey<Item> BLACKLIST = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(QualityFood.MODID, "blacklist"));

    public QFItemTags(final DataGenerator generator, final BlockTagsProvider provider, final ExistingFileHelper helper) {
        super(generator, provider, QualityFood.MODID, helper);
    }

    @Override
    protected void addTags() {
        tag(MATERIAL_WHITELIST)
                .addTag(Tags.Items.EGGS)
                .addTag(Tags.Items.SEEDS)
                .addTag(Tags.Items.CROPS)
                .addTag(Tags.Items.MUSHROOMS)
                .addOptionalTag(Compat.forge("dough"))
                .addOptionalTag(Compat.forge("flour"))
                .addOptionalTag(Compat.farmersdelight("wild_crops"))
                .add(Items.SUGAR)
                .add(Items.SUGAR_CANE)
                .add(Items.COCOA_BEANS)
                .add(Items.INK_SAC) // Farmer's Delight
                .addOptional(Compat.farmersdelight("rice_panicle"))
                /* Storage blocks */
                .add(Items.HAY_BLOCK)
                .add(Items.HONEY_BLOCK)
                .addOptional(Compat.farmersdelight("carrot_crate"))
                .addOptional(Compat.farmersdelight("potato_crate"))
                .addOptional(Compat.farmersdelight("beetroot_crate"))
                .addOptional(Compat.farmersdelight("cabbage_crate"))
                .addOptional(Compat.farmersdelight("tomato_crate"))
                .addOptional(Compat.farmersdelight("onion_crate"))
                .addOptional(Compat.farmersdelight("chorus_crate"))
                .addOptional(Compat.farmersdelight("rice_bale"))
                .addOptional(Compat.farmersdelight("rice_bag"))
                .addOptional(Compat.quark("apple_crate"))
                .addOptional(Compat.quark("beetroot_crate"))
                .addOptional(Compat.quark("berry_sack"))
                .addOptional(Compat.quark("carrot_crate"))
                .addOptional(Compat.quark("chorus_fruit_block"))
                .addOptional(Compat.quark("cocoa_beans_sack"))
                .addOptional(Compat.quark("glowberry_sack"))
                .addOptional(Compat.quark("golden_apple_crate"))
                .addOptional(Compat.quark("golden_carrot_crate"))
                .addOptional(Compat.quark("potato_crate"))
                .addOptional(Compat.quark("sugar_cane_block"))
                .addOptional(Compat.supplementaries("sugar_cube"))
                .addOptional(Compat.vinery("white_grape_crate"))
                .addOptional(Compat.vinery("red_grape_crate"))
                .addOptional(Compat.vinery("cherry_crate"))
                .addOptional(Compat.vinery("apple_crate"));

        tag(BLACKLIST)
                .addTag(ItemTags.FLOWERS)
                .addTag(ItemTags.SAPLINGS)
                .add(Items.GRASS)
                .add(Items.TALL_GRASS)
                .add(Items.FERN)
                .add(Items.LARGE_FERN)
                .add(Items.SMALL_DRIPLEAF)
                .add(Items.BIG_DRIPLEAF)
                .add(Items.LILY_PAD)
                .add(Items.DEAD_BUSH)
                .add(Items.VINE)
                .add(Items.NETHER_WART) // Forbidden & Arcanus
                .add(Items.SEAGRASS) // Ocean's Delight | Aquaculture 2
                .add(Items.WARPED_ROOTS) // Nether's Delight
                .add(Items.CRIMSON_ROOTS) // Nether's Delight
                .add(Items.NETHER_SPROUTS)
                .addOptional(Compat.supplementaries("flax"))
                .addOptional(Compat.supplementaries("flax_seeds"));
    }
}