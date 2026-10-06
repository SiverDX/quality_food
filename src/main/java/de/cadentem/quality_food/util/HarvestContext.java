package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Applies quality to harvested items (from blocks like crops or plants)
 * @param stack The item to which the quality will be applied to
 * @param position The position at which the block was harvested
 * @param state The block that was harvested
 * @param farmland The farmland block (i.e., the block the crop or plant is growing on)
 * @param player The player that harvested the block
 * @param quality The stored quality of the harvested block
 */
public record HarvestContext(ItemStack stack, @Nullable BlockPos position, @Nullable BlockState state, @Nullable BlockState farmland, @Nullable Player player, ServerLevel level, Quality quality) {
    public static Builder create(final ItemStack stack, final Level level) {
        return new Builder(stack, level);
    }

    public static class Builder {
        private final ItemStack stack;
        private final @Nullable ServerLevel level;
        private @Nullable BlockPos position;
        private @Nullable BlockState state;
        private @Nullable BlockState farmland;
        private @Nullable Player player;
        private Quality quality = Quality.NONE;

        private Builder(final ItemStack stack, final Level level) {
            this.stack = stack;
            this.level = level instanceof ServerLevel serverLevel ? serverLevel : null;
        }

        public Builder position(@Nullable final BlockPos position) {
            this.position = position;
            return this;
        }

        public Builder state(@Nullable final BlockState state) {
            this.state = state;
            return this;
        }

        public Builder farmland(@Nullable final BlockState farmland) {
            this.farmland = farmland;
            return this;
        }

        public Builder player(@Nullable final Player player) {
            this.player = player;
            return this;
        }

        /** @return null if the level is not a server level or the item is not valid for quality */
        public @Nullable HarvestContext build() {
            if (level == null || !Utils.isValidItem(stack)) {
                return null;
            }

            if (position != null) {
                state = Objects.requireNonNullElse(state, level.getBlockState(position));
                farmland = Objects.requireNonNullElseGet(farmland, () -> {
                    // For safety only check up to 10 blocks
                    // TODO :: needs a flag whether farmland *should* be checked? e.g. for vines
                    BlockState found = null;

                    for (int depth = 0; depth < 10; depth++) {
                        BlockPos below = position.below(depth);
                        found = level.getBlockState(below);

                        if (found.isFaceSturdy(level, below, Direction.UP)) {
                            break;
                        }
                    }

                    return found;
                });
                quality = LevelData.get(level, position, true);
            }

            return new HarvestContext(stack, position, state, farmland, player, level, quality);
        }
    }
}
