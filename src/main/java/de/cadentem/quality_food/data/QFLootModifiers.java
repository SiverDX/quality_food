package de.cadentem.quality_food.data;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class QFLootModifiers extends GlobalLootModifierProvider {
    public QFLootModifiers(final DataGenerator generator) {
        super(generator, QualityFood.MODID);
    }

    @Override
    protected void start() {

    }
}
