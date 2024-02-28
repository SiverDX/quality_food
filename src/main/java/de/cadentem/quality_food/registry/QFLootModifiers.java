package de.cadentem.quality_food.registry;

import com.mojang.serialization.Codec;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.loot_modifiers.QualityLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class QFLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, QualityFood.MODID);

    @SuppressWarnings("unused")
    public static final RegistryObject<Codec<QualityLootModifier>> QUALITY_LOOT_MODIFIER = LOOT_MODIFIERS.register(QualityLootModifier.ID, () -> QualityLootModifier.CODEC);
}
