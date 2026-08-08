package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.attachments.BlockData;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.data.QFBlockTags;
import de.cadentem.quality_food.data.QFItemTags;
import de.cadentem.quality_food.network.CookingParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;

public class Utils {
    public static boolean isValidItem(final ItemStack stack) {
        return isValidItem(stack, true);
    }

    public static boolean isValidItem(final ItemStack stack, boolean checkBlock) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.is(QFItemTags.BLACKLIST)) {
            return false;
        }

        FoodProperties properties = stack.getFoodProperties(null);

        if (properties != null && (properties.nutrition() > 0 || properties.saturation() > 0)) {
            return true;
        }

        if (checkBlock && stack.getItem() instanceof BlockItem blockItem && isValidBlock(blockItem.getBlock(), false)) {
            return true;
        }

        return stack.is(QFItemTags.MATERIAL_WHITELIST);
    }

    public static boolean isValidBlock(final Block block) {
        return isValidBlock(block, true);
    }

    @SuppressWarnings("deprecation")
    public static boolean isValidBlock(final Block block, boolean checkItem) {
        if (block.builtInRegistryHolder().is(QFBlockTags.QUALITY_BLOCKS)) {
            return true;
        } else if (checkItem) {
            return isValidItem(block.asItem().getDefaultInstance(), false);
        }

        return false;
    }

    // FIXME :: adjust particle count
    public static void sendParticles(final ServerLevel serverLevel, final BlockEntity furnace, final BlockPos position) {
        int tickOffset = serverLevel.getRandom().nextInt(-3, 3);

        if (serverLevel.getGameTime() % (10 + tickOffset) == 0) {
            BlockData data = furnace.getData(AttachmentHandler.BLOCK_DATA);
            double qualityBonus = data.getQuality();

            if (qualityBonus > 0.1) {
                PacketDistributor.sendToPlayersNear(serverLevel, null, position.getX(), position.getY(), position.getZ(), 64, new CookingParticles(position, qualityBonus));
            }
        }
    }

    /**
     * Store quality of a crop that has grown in another direction
     * @param grown The new state
     * @param position The position of the base crop (not the newly grown position), to retrieve the current quality
     * @param direction The position in which the crop grows
     */
    public static void storeQuality(final BlockState grown, final ServerLevel accessor, final BlockPos position, final Direction direction) {
        storeQuality(grown, accessor, position, position.relative(direction), 1);
    }

    public static void storeQuality(final BlockState grown, final ServerLevel accessor, final BlockPos position, final BlockPos grownPosition) {
        storeQuality(grown, accessor, position, grownPosition, 1);
    }

    /**
     * Used to store quality to a new position (based on the quality of the passed position) <br>
     * Usually used to store quality to a crop which has a higher height than 1 when growing
     */
    public static void storeQuality(final BlockState grown, final ServerLevel level, final BlockPos position, final BlockPos grownPosition, double chance) {
        if (Utils.isValidBlock(grown.getBlock())) {
            LevelData data = level.getData(AttachmentHandler.LEVEL_DATA);
            Quality quality = data.get(position);

            if (Math.random() > chance) {
                quality = Quality.getRandom(ItemStack.EMPTY, quality.level() - 1);
            }

            if (/* Don't apply PLAYER_PLACED */ quality.level() > 0) {
                data.set(grownPosition, quality);
            }
        }
    }

    /**
     * Collects quality-applicable items from the given inventory
     * @param maxSlotCheck To determine up to which slot the items should be considered
     */
    public static Collection<ItemStack> collectIngredients(final ItemStackHandler handler, final Supplier<Integer> maxSlotCheck) {
        return collectIngredients(handler::getStackInSlot, maxSlotCheck);
    }

    /**
     * Collects quality-applicable items from the given inventory
     * @param maxSlotCheck To determine up to which slot the items should be considered
     */
    public static Collection<ItemStack> collectIngredients(final SimpleContainer container, final Supplier<Integer> maxSlotCheck) {
        return collectIngredients(container::getItem, maxSlotCheck);
    }

    private static Collection<ItemStack> collectIngredients(final Function<Integer, ItemStack> itemSupplier, final Supplier<Integer> maxSlotCheck) {
        List<ItemStack> ingredients = new ArrayList<>();

        for (int slot = 0; slot < maxSlotCheck.get(); slot++) {
            ItemStack ingredient = itemSupplier.apply(slot);

            if (!Utils.isValidItem(ingredient)) {
                continue;
            }

            ingredients.add(ingredient);
        }

        return ingredients;
    }

    /**
     * Collects the cooking bonus from the ingredients and the lowest quality type present </br>
     * This is then added to the cooking queue which will be used to apply the cooking bonus / quality to the result item
     */
    public static void incrementQuality(final BlockEntity blockEntity, @Unmodifiable final Collection<ItemStack> ingredients, int resultStackSize) {
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide() || ingredients.isEmpty()) {
            return;
        }

        Set<Holder<QualityType>> qualities = new TreeSet<>(Comparator.comparingInt(quality -> quality.value().level()));
        double qualityBonus = 0;

        for (ItemStack ingredient : ingredients) {
            Holder<QualityType> type = QualityUtils.getType(ingredient);

            if (type.value() != QualityType.NONE) {
                qualityBonus += type.value().cookingBonus() / ingredients.size();
            }

            qualities.add(type);
        }

        BlockData data = blockEntity.getData(AttachmentHandler.BLOCK_DATA);

        for (int i = 0; i < resultStackSize; i++) {
            data.addQualityEntry(qualities.stream().findFirst().orElse(Holder.direct(QualityType.NONE)), qualityBonus);
        }

        blockEntity.setChanged();
    }

    public static void useQuality(final BlockEntity block, final ItemStack stack, @Nullable final Player player) {
        BlockData data = block.getData(AttachmentHandler.BLOCK_DATA);
        //noinspection DataFlowIssue -> level is present
        data.useQuality(stack, player, block.getLevel());
        block.setChanged();
    }
}
