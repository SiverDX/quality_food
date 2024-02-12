package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.loot_modifiers.HarvestLootModifier;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class QFLootModifiers extends GlobalLootModifierProvider {
    public QFLootModifiers(final DataGenerator generator) {
        super(generator, QualityFood.MODID);
    }

    @Override
    protected void start() {
        add(HarvestLootModifier.ID, new HarvestLootModifier(new LootItemCondition[]{}));
    }
}
