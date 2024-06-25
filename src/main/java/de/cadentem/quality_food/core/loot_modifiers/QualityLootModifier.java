package de.cadentem.quality_food.core.loot_modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class QualityLootModifier extends LootModifier {
    public static final String ID = "quality_loot_modifier";
    public static final Codec<QualityLootModifier> CODEC = RecordCodecBuilder.create(instance -> LootModifier.codecStart(instance).apply(instance, QualityLootModifier::new));

    public QualityLootModifier(final LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(final ObjectArrayList<ItemStack> generatedLoot, final LootContext context) {
        if (generatedLoot.isEmpty() || !isValidLootTable(context)) {
            return generatedLoot;
        }

        generatedLoot.stream().filter(Utils::isValidItem).forEach(stack -> QualityUtils.applyQuality(stack, context.getParamOrNull(LootContextParams.THIS_ENTITY)));
        return generatedLoot;
    }

    private boolean isValidLootTable(final LootContext context) {
        return context.getQueriedLootTableId().getPath().startsWith("chest");
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}