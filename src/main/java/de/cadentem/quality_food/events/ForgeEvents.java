package de.cadentem.quality_food.events;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.config.ClientConfig;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.core.commands.QualityArgument;
import de.cadentem.quality_food.core.commands.QualityItemArgument;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber
public class ForgeEvents {
    private static final DecimalFormat FORMAT = new DecimalFormat("###.##");

    @SubscribeEvent
    public static void handleRightClick(final PlayerInteractEvent.RightClickBlock event) {
        BlockPos position = event.getHitVec().getBlockPos();

        if (event.getLevel().getBlockEntity(position) != null) {
            Utils.BLOCK_ENTITY_POSITION.set(position);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleFishing(final ItemFishedEvent event) {
        if (event.getEntity().getLevel().isClientSide()) {
            return;
        }

        event.getDrops().forEach(drop -> QualityUtils.applyQuality(drop, event.getEntity()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleLoot(final LivingDropsEvent event) {
        Entity attacker = event.getSource().getEntity();

        if (attacker == null || /* Player death should not grant quality */ event.getEntity() instanceof Player) {
            return;
        }

        if (attacker instanceof LivingEntity livingAttacker) {
            event.getDrops().forEach(drop -> QualityUtils.applyQuality(drop.getItem(), livingAttacker));
        }
    }

    @SubscribeEvent
    public static void handleCrafting(final PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().getLevel().isClientSide()) {
            return;
        }

        int size = event.getInventory().getContainerSize();
        float bonus = 0;

        for (int slot = 0; slot < size; slot++) {
            bonus += QualityUtils.getBonus(QualityUtils.getQuality(event.getInventory().getItem(slot)));
        }

        QualityUtils.applyQuality(event.getCrafting(), event.getEntity(), Bonus.additive(bonus));
    }

    @SubscribeEvent
    public static void addTooltip(final ItemTooltipEvent event) {
        if (ClientConfig.SPEC.isLoaded() && !ClientConfig.EFFECT_TOOLTIPS.get()) {
            return;
        }

        FoodProperties foodProperties = event.getItemStack().getFoodProperties(event.getEntity());

        if (foodProperties != null) {
            List<Pair<MobEffectInstance, Float>> effectData = foodProperties.getEffects();

            for (Pair<MobEffectInstance, Float> data : effectData) {
                MobEffectInstance effect = data.getFirst();
                MutableComponent effectTooltip = Component.translatable(effect.getDescriptionId());

                if (effect.getAmplifier() > 0) {
                    effectTooltip = Component.translatable("potion.withAmplifier", effectTooltip, Component.translatable("potion.potency." + effect.getAmplifier()));
                }

                if (effect.getDuration() > 20) {
                    effectTooltip = Component.translatable("potion.withDuration", effectTooltip, MobEffectUtil.formatDuration(effect, 1));
                }

                ChatFormatting formatting = effect.getEffect().getCategory().getTooltipFormatting();
                event.getToolTip().remove(effectTooltip.withStyle(formatting));
                effectTooltip = Component.translatable("potion.withProbability", effectTooltip, FORMAT.format(data.getSecond() * 100) + "%").withStyle(formatting);

                if (!event.getToolTip().contains(effectTooltip)) {
                    event.getToolTip().add(effectTooltip);
                }
            }
        }
    }

    /** Mostly a copy of {@link net.minecraft.server.commands.GiveCommand#register(CommandDispatcher, CommandBuildContext)} */
    @SubscribeEvent
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
        );
    }

    /** Mostly a copy of {@link net.minecraft.server.commands.GiveCommand#giveItem(CommandSourceStack, ItemInput, Collection, int)} */
    private static int giveItem(final CommandSourceStack source, final ItemInput input, final Collection<ServerPlayer> players, int count, final Quality quality) throws CommandSyntaxException {
        int maxStackSize = input.getItem().getMaxStackSize();
        int maxCount = maxStackSize * /* MAX_ALLOWED_ITEMSTACKS */ 100;
        ItemStack tempStack = input.createItemStack(1, false);

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

                if (!QualityUtils.hasQuality(stack)) {
                    source.sendFailure(Component.translatable("commands.quality_food.quality.failed.no_quality", stack.getDisplayName()));
                    return 0;
                }

                boolean wasAdded = player.getInventory().add(stack);

                if (wasAdded && stack.isEmpty()) {
                    stack.setCount(1);
                    ItemEntity entity = player.drop(stack, false);

                    if (entity != null) {
                        entity.makeFakeItem();
                    }

                    player.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, (player.getRandom().nextFloat() - player.getRandom().nextFloat() * 0.7f + 1) * 2);
                    player.containerMenu.broadcastChanges();
                } else {
                    ItemEntity entity = player.drop(stack, false);

                    if (entity != null) {
                        entity.setNoPickUpDelay();
                        entity.setOwner(player.getUUID());
                    }
                }
            }
        }

        if (players.size() == 1) {
            source.sendSuccess(Component.translatable("commands.give.success.single", count, tempStack.getDisplayName(), players.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(Component.translatable("commands.give.success.single", count, tempStack.getDisplayName(), players.size()), true);
        }

        return players.size();
    }
}
