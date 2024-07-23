package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.loot_modifiers.QualityLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class QFLootModifiers extends GlobalLootModifierProvider {
    public QFLootModifiers(final PackOutput output, final CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, QualityFood.MODID);
    }

    @Override
    protected void start() {
        add(QualityLootModifier.ID, new QualityLootModifier(new LootItemCondition[]{}));
    }
}
