package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.compat.Compat;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class QFItemTags extends ItemTagsProvider {
    public static final TagKey<Item> MATERIAL_WHITELIST = ItemTags.create(QualityFood.location("material_whitelist"));
    public static final TagKey<Item> BLACKLIST = ItemTags.create(QualityFood.location("blacklist"));

    public QFItemTags(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider, final CompletableFuture<TagLookup<Block>> blockTags, @Nullable final ExistingFileHelper helper) {
        super(output, provider, blockTags, QualityFood.MODID, helper);
    }

    @Override
    protected void addTags(@NotNull final HolderLookup.Provider provider) {
        tag(MATERIAL_WHITELIST)
                .addTag(Tags.Items.MUSHROOMS)
                .addTag(Tags.Items.CROPS)
                .addTag(Tags.Items.SEEDS)
                .addTag(Tags.Items.EGGS)
                .add(Items.COCOA_BEANS)
                .add(Items.SUGAR_CANE)
                .add(Items.SUGAR)
                .add(Items.INK_SAC) // Farmer's Delight
                .add(Items.HAY_BLOCK)
                .add(Items.HONEY_BLOCK)
                .addOptionalTag(Compat.forge("dough"))
                .addOptionalTag(Compat.forge("flour"))
                .addOptionalTag(Compat.farmersdelight("wild_crops"))
                .addOptional(Compat.farmersdelight("rice_panicle"))
                /* Storage blocks */
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
                .addOptional(Compat.vinery("white_grape_bag"))
                .addOptional(Compat.vinery("red_grape_bag"))
                .addOptional(Compat.vinery("cherry_bag"))
                .addOptional(Compat.vinery("apple_bag"));

        tag(BLACKLIST)
                .addOptional(Compat.supplementaries("flax"))
                .addOptional(Compat.supplementaries("flax_seeds"));
    }
}