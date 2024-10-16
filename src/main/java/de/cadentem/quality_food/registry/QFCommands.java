package de.cadentem.quality_food.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.core.commands.QualityArgument;
import de.cadentem.quality_food.core.commands.QualityItemArgument;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.registries.DeferredRegister;

import java.util.Collection;

public class QFCommands {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENTS = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, QualityFood.MODID);

    static {
        COMMAND_ARGUMENTS.register("quality", () -> ArgumentTypeInfos.registerByClass(QualityArgument.class, SingletonArgumentInfo.contextFree(QualityArgument::new)));
        COMMAND_ARGUMENTS.register("item", () -> ArgumentTypeInfos.registerByClass(QualityItemArgument.class, SingletonArgumentInfo.contextAware(QualityItemArgument::item)));
    }

    /**
     * Mostly a copy of {@link net.minecraft.server.commands.GiveCommand#register(CommandDispatcher, CommandBuildContext)}
     */
    public static void registerCommands(final RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal(QualityFood.MODID)
                        .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(
                                Commands.literal("give")
                                        .then(
                                                Commands.argument("targets", EntityArgument.players())
                                                        .then(
                                                                Commands.argument("quality", new QualityArgument())
                                                                        .then(
                                                                                Commands.argument("item", QualityItemArgument.item(event.getBuildContext()))
                                                                                        .executes(context -> giveItem(context.getSource(), ItemArgument.getItem(context, "item"), EntityArgument.getPlayers(context, "targets"), 1, QualityArgument.get(context)))
                                                                                        .then(
                                                                                                Commands.argument("count", IntegerArgumentType.integer(1))
                                                                                                        .executes(context -> giveItem(context.getSource(), ItemArgument.getItem(context, "item"), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "count"), QualityArgument.get(context)))
                                                                                        )
                                                                        )
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("apply")
                                        .then(
                                                Commands.argument("quality", new QualityArgument())
                                                        .executes(context -> applyQuality(context.getSource(), QualityArgument.get(context), false))
                                                        .then(Commands.argument("override", BoolArgumentType.bool())
                                                                .executes(context -> applyQuality(context.getSource(), QualityArgument.get(context), BoolArgumentType.getBool(context, "override")))
                                                        )
                                        )
                        )
                        .then(Commands.literal("remove").executes(context -> removeQuality(context.getSource())))
        );
    }

    private static int applyQuality(final CommandSourceStack source, final Quality quality, boolean shouldOverride) {
        if (quality.level() == 0) {
            source.sendFailure(Component.translatable("commands.quality_food.quality.failed.invalid_quality"));
            return 0;
        }

        if (source.getEntity() instanceof LivingEntity livingSource) {
            ItemStack stack = livingSource.getMainHandItem();

            if (!Utils.isValidItem(stack)) {
                source.sendFailure(Component.translatable("commands.quality_food.quality.failed.no_quality", stack.getDisplayName()));
                return 0;
            }

            if (QualityUtils.hasQuality(stack) && !shouldOverride) {
                source.sendFailure(Component.translatable("commands.quality_food.quality.failed.already_has_quality", stack.getDisplayName()));
                return 0;
            }

            if (shouldOverride && stack.getTag() != null) {
                stack.getTag().remove(QualityUtils.QUALITY_TAG);
            }

            QualityUtils.applyQuality(stack, quality);
            return 1;
        }

        return 0;
    }

    private static int removeQuality(final CommandSourceStack source) {
        if (source.getEntity() instanceof LivingEntity livingSource) {
            ItemStack stack = livingSource.getMainHandItem();

            if (stack.getTag() != null) {
                stack.getTag().remove(QualityUtils.QUALITY_TAG);
                return 1;
            } else {
                source.sendFailure(Component.translatable("commands.quality_food.quality.failed.missing_quality", stack.getDisplayName()));
                return 0;
            }
        }

        return 0;
    }

    /**
     * Mostly a copy from {@link net.minecraft.server.commands.GiveCommand}
     */
    private static int giveItem(final CommandSourceStack source, final ItemInput input, final Collection<ServerPlayer> players, int count, final Quality quality) throws CommandSyntaxException {
        if (quality.level() == 0) {
            source.sendFailure(Component.translatable("commands.quality_food.quality.failed.invalid_quality"));
            return 0;
        }

        int maxStackSize = input.getItem().getMaxStackSize();
        int maxCount = maxStackSize * /* MAX_ALLOWED_ITEMSTACKS */ 100;
        ItemStack tempStack = input.createItemStack(1, false);

        if (QualityUtils.isInvalidItem(tempStack)) {
            source.sendFailure(Component.translatable("commands.quality_food.quality.failed.no_quality", tempStack.getDisplayName()));
            return 0;
        }

        if (count > maxCount) {
            source.sendFailure(Component.translatable("commands.give.failed.toomanyitems", maxCount, tempStack.getDisplayName()));
            return 0;
        }

        for (ServerPlayer player : players) {
            int toGive = count;

            while (toGive > 0) {
                int min = Math.min(toGive, maxStackSize);
                toGive -= min;

                ItemStack stack = input.createItemStack(min, false);
                QualityUtils.applyQuality(stack, quality);
                boolean wasAdded = player.getInventory().add(stack);

                if (wasAdded && stack.isEmpty()) {
                    stack.setCount(1);
                    ItemEntity entity = player.drop(stack, false);

                    if (entity != null) {
                        entity.makeFakeItem();
                    }

                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, (player.getRandom().nextFloat() - player.getRandom().nextFloat() * 0.7f + 1) * 2);
                    player.containerMenu.broadcastChanges();
                } else {
                    ItemEntity entity = player.drop(stack, false);

                    if (entity != null) {
                        entity.setNoPickUpDelay();
                        entity.setTarget(player.getUUID());
                    }
                }
            }
        }

        if (players.size() == 1) {
            source.sendSuccess(() -> Component.translatable("commands.give.success.single", count, tempStack.getDisplayName(), players.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.give.success.single", count, tempStack.getDisplayName(), players.size()), true);
        }

        return players.size();
    }
}
