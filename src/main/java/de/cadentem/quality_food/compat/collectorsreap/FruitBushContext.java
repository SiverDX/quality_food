package de.cadentem.quality_food.compat.collectorsreap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record FruitBushContext(BlockState state, Level level, BlockPos position, Player player) { /* Nothing to do */ }
