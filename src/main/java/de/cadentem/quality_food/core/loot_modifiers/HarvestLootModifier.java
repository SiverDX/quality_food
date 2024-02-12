package de.cadentem.quality_food.core.loot_modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.util.RarityUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.Tags;
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
        if (!context.hasParam(LootContextParams.BLOCK_STATE)) {
            return generatedLoot;
        }

        BlockState state = context.getParam(LootContextParams.BLOCK_STATE);

        if (state.is(BlockTags.CROPS)) {
            Entity entity = context.hasParam(LootContextParams.THIS_ENTITY) ? context.getParam(LootContextParams.THIS_ENTITY) : null;
            float luck = entity instanceof Player player ? player.getLuck() : 0;
            generatedLoot.stream().filter(stack -> stack.is(Tags.Items.CROPS)).forEach(stack -> RarityUtils.applyRarity(stack, context.getRandom(), luck));
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}