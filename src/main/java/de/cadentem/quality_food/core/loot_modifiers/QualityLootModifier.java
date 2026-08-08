package de.cadentem.quality_food.core.loot_modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class QualityLootModifier extends LootModifier {
    public static final String ID = "quality_loot_modifier";
    public static final MapCodec<QualityLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> LootModifier.codecStart(instance).apply(instance, QualityLootModifier::new));

    // Otherwise quality will be double-checked on most blocks due to the 'popResource' inject in 'BlockMixin' (this modifier will trigger first)
    // That injection is needed to handle with right-click harvesting like glow berries, which do not trigger the loot collection event
    public static BlockPos lastProcessedPosition;

    public QualityLootModifier(final LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(final ObjectArrayList<ItemStack> generatedLoot, @NotNull final LootContext context) {
        if (generatedLoot.isEmpty()) {
            return generatedLoot;
        }

        Player playerReference = context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Player player ? player : null;
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);

        BlockPos position;
        BlockState farmland;
        Quality quality;

        if (origin != null) {
            position = BlockPos.containing(origin);
            farmland = context.getLevel().getBlockState(position.below());
            quality = LevelData.get(context.getLevel(), position, true);
        } else {
            position = null;
            farmland = null;
            quality = Quality.NONE;
        }

        generatedLoot.stream().filter(Utils::isValidItem).forEach(stack -> {
            if (state != null && position != null) {
                QualityUtils.applyQuality(stack, state, quality, playerReference, farmland, context.getLevel().registryAccess());
                lastProcessedPosition = position;
            } else {
                QualityUtils.applyQuality(stack, playerReference, context.getLevel().registryAccess());
            }
        });

        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}