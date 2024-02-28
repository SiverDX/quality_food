package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.loot_modifiers.QualityLootModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class QFLootModifiers extends GlobalLootModifierProvider {
    public QFLootModifiers(final PackOutput output) {
        super(output, QualityFood.MODID);
    }

    @Override
    protected void start() {
        add(QualityLootModifier.ID, new QualityLootModifier(new LootItemCondition[]{}));
    }
}
