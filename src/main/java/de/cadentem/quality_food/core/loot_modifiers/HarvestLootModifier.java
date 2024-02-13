package de.cadentem.quality_food.core.loot_modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class HarvestLootModifier extends LootModifier {
    public static final String ID = "harvest_loot_modifier";
    public static final Codec<HarvestLootModifier> CODEC = RecordCodecBuilder.create(instance -> LootModifier.codecStart(instance).apply(instance, HarvestLootModifier::new));

    public HarvestLootModifier(final LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(final ObjectArrayList<ItemStack> generatedLoot, final LootContext context) {
        BlockState state = context.hasParam(LootContextParams.BLOCK_STATE) ? context.getParam(LootContextParams.BLOCK_STATE) : null;
        Quality quality = state != null && state.hasProperty(Utils.QUALITY_STATE) ? Quality.get(state.getValue(Utils.QUALITY_STATE)) : Quality.NONE;

        Entity entity = context.hasParam(LootContextParams.THIS_ENTITY) ? context.getParam(LootContextParams.THIS_ENTITY) : null;
        float luck = entity instanceof Player player ? player.getLuck() : 0;

        generatedLoot.stream().filter(Utils::isValidItem).forEach(stack -> {
            if (quality != Quality.NONE) {
                QualityUtils.applyQuality(stack, quality);
            } else {
                QualityUtils.applyQuality(stack, context.getRandom(), luck);
            }
        });

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}