package de.cadentem.quality_food.data;

import net.minecraft.tags.TagEntry;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.compat.Compat;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class QFItemTags extends IntrinsicHolderTagsProvider<Item> {
    public static final TagKey<Item> MATERIAL_WHITELIST = ItemTags.create(QualityFood.location("material_whitelist"));
    public static final TagKey<Item> BLACKLIST = ItemTags.create(QualityFood.location("blacklist"));

    public QFItemTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.ITEM, provider, item -> item.builtInRegistryHolder().key(), QualityFood.MODID);
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
                .addTag(Tags.Items.FOODS)
                .addTag(Tags.Items.MUSHROOMS)
                .addOptionalTag(TagKey.create(Registries.ITEM, Compat.common("flours"))) // Create
                .addOptionalTag(TagKey.create(Registries.ITEM, Compat.common("doughs"))) // Create
                .addOptionalTag(TagKey.create(Registries.ITEM, Compat.common("dough"))) // Kaleidoscope Cookery
                .addOptionalTag(TagKey.create(Registries.ITEM, Compat.location(Compat.Mod.FARMERSDELIGHT.modid(), "wild_crops")))
                .addOptionalTag(TagKey.create(Registries.ITEM, Compat.location(Compat.Mod.FARMERSDELIGHT.modid(), "pies")))
                .addOptionalTag(TagKey.create(Registries.ITEM, Compat.location(Compat.Mod.FARMERSDELIGHT.modid(), "mushroom_colonies")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.FARMERSDELIGHT.modid(), "rice_panicle")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.FARMERSDELIGHT.modid(), "debug_pumpkin_pie")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.FRUITFUL_FUN.modid(), "lemon_roast_chicken_block")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.COLLECTORS_REAP.modid(), "pomegranate")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.FARM_AND_CHARM.modid(), "barley")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.FARM_AND_CHARM.modid(), "oat")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.HERALBREWS.modid(), "hibiscus")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.HERALBREWS.modid(), "lavender_blossom")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.HERALBREWS.modid(), "green_tea_leaf")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.KALEIDOSCOPE_COOKERY.modid(), "rice_panicle")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.VINTAGEDELIGHT.modid(), "cheese_wheel")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.VINTAGEDELIGHT.modid(), "oat")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "unripe_cheddar_cheese_wheel")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "cheddar_cheese_wheel")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "unripe_goat_cheese_wheel")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "goat_cheese_wheel")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "blueberry_pie")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "raspberry_pie")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "grape_pie")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "peanut_butter_pie")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "chicken_pot_pie")))
                .add(TagEntry.optionalElement(Compat.location("hearthandharvest", "carrot_cake")))
        ;

        tag(BLACKLIST)
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.SUPPLEMENTARIES.modid(), "flax")))
                .add(TagEntry.optionalElement(Compat.location(Compat.Mod.SUPPLEMENTARIES.modid(), "flax_seeds")));
    }
}
