package de.cadentem.quality_food.core.loot_modifiers;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QualityLootModifier extends LootModifier {
    public static final String ID = "quality_loot_modifier";
    public static final Serializer SERIALIZER = new Serializer();

    public QualityLootModifier(final LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull List<ItemStack> doApply(final List<ItemStack> generatedLoot, final LootContext context) {
        if (generatedLoot.isEmpty() || !isValidLootTable(context)) {
            return generatedLoot;
        }

        generatedLoot.stream().filter(Utils::isValidItem).forEach(stack -> QualityUtils.applyQuality(stack, context.getParamOrNull(LootContextParams.THIS_ENTITY)));
        return generatedLoot;
    }

    private boolean isValidLootTable(final LootContext context) {
        return context.getQueriedLootTableId().getPath().startsWith("chest");
    }

    public static class Serializer extends GlobalLootModifierSerializer<QualityLootModifier> {
        @Override
        public QualityLootModifier read(final ResourceLocation location, final JsonObject json, final LootItemCondition[] conditions) {
            return new QualityLootModifier(conditions);
        }

        @Override
        public JsonObject write(final QualityLootModifier modifier) {
            return makeConditions(modifier.conditions);
        }
    }
}