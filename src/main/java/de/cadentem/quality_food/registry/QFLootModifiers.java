package de.cadentem.quality_food.registry;

import com.mojang.serialization.MapCodec;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.loot_modifiers.QualityLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class QFLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, QualityFood.MODID);

    static {
        LOOT_MODIFIERS.register(QualityLootModifier.ID, () -> QualityLootModifier.CODEC);
    }
}
