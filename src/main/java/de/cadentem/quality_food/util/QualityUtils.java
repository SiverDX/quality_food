package de.cadentem.quality_food.util;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.compat.Compat;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.registry.QFComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.WildCropBlock;

import java.util.*;
import java.util.function.Predicate;

public class QualityUtils {
    private static final RandomSource RANDOM = RandomSource.create();

    public static boolean hasQuality(final ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        Quality quality = stack.get(QFComponents.QUALITY_DATA_COMPONENT);

        if (quality == null) {
            return false;
        }

        return quality.level() > 0;
    }

    /**
     * @param slots       Slots which may contain crafting materials with quality (will apply a bonus)
     * @param isSlotValid To test whether the slot is relevant or not (since the list usually contains the inventory as well)
     */
    public static float getQualityBonus(final List<Slot> slots, final Predicate<Slot> isSlotValid) {
        int validIngredients = 0;
        float bonus = 0;

        for (Slot slot : slots) {
            if (isSlotValid.test(slot) && Utils.isValidItem(slot.getItem())) {
                validIngredients++;
            }
        }

        if (validIngredients == 0) {
            return 0;
        }

        for (Slot slot : slots) {
            if (isSlotValid.test(slot)) {
                QualityType type = QualityUtils.getType(slot.getItem());

                if (type != QualityType.NONE) {
                    bonus += (float) (type.craftingBonus() / validIngredients);
                }
            }
        }

        return bonus;
    }

    public static float getQualityBonus(final CraftingContainer container) {
        int validIngredients = QualityUtils.countIngredients(container);

        if (validIngredients == 0) {
            return 0;
        }

        float bonus = 0;

        for (ItemStack ingredient : container.getItems()) {
            QualityType type = QualityUtils.getType(ingredient);

            if (type != QualityType.NONE) {
                bonus += (float) (type.craftingBonus() / validIngredients);
            }
        }

        return bonus;
    }

    public static void applyQuality(final ItemStack stack) {
        applyQuality(stack, null, Bonus.DEFAULT);
    }

    public static void applyQuality(final ItemStack stack, @NotNull final Bonus bonus) {
        applyQuality(stack, null, bonus);
    }

    public static void applyQuality(final ItemStack stack, @Nullable final Entity entity) {
        applyQuality(stack, entity, Bonus.DEFAULT);
    }

    public static void applyQuality(final ItemStack stack, @Nullable final Entity entity, @NotNull final Bonus bonus) {
        List<Bonus> bonusList = new ArrayList<>();
        bonusList.add(bonus);
        applyQuality(stack, entity, bonusList);
    }

    public static void applyQuality(final ItemStack stack, @Nullable final Entity entity, @NotNull final List<Bonus> bonusList) {
        if (Utils.LAST_STACK.get() == stack) {
            return;
        }

        Utils.LAST_STACK.set(stack);

        RandomSource random = entity instanceof LivingEntity livingEntity ? livingEntity.getRandom() : RANDOM;
        double rolls = 1 + (entity instanceof Player player ? player.getLuck() * ServerConfig.LUCK_MULTIPLIER.get() : 0);

        if (rolls < 0) {
            rolls = 0.1;
        }

        HolderLookup.RegistryLookup<QualityType> lookup = CommonHooks.resolveLookup(QFComponents.QUALITY_TYPE_REGISTRY);

        if (lookup == null) {
            return;
        }

        List<QualityType> types = new ArrayList<>();
        lookup.listElements().forEach(entry -> types.add(entry.value()));
        types.sort(Comparator.comparingInt(QualityType::level).reversed());

        float roll = random.nextFloat();
        int fullRolls = (int) rolls;

        if (random.nextDouble() <= (rolls - fullRolls)) {
            fullRolls++;
        }

        for (int i = 0; i < fullRolls; i++) {
            for (QualityType type : types) {
                double chance = type.chance();

                for (Bonus bonus : bonusList) {
                    chance = switch (bonus.type()) {
                        case ADDITIVE -> chance + bonus.amount();
                        case MULTIPLICATIVE -> chance * bonus.amount();
                    };
                }

                if (roll <= chance) {
                    boolean wasApplied = applyQuality(stack, type.createQuality(stack));

                    if (wasApplied) {
                        break;
                    }
                }
            }
        }
    }

    public static boolean applyQuality(final ItemStack stack, final QualityType type) {
        return applyQuality(stack, type.createQuality(stack));
    }

    /**
     * @param stack   The item to apply quality to
     * @param quality The quality to directly set
     * @return If the quality was successfully set true otherwise false
     */
    public static boolean applyQuality(final ItemStack stack, final Quality quality) {
        if (!isValidQuality(quality) || isInvalidItem(stack)) {
            return false;
        }

        stack.set(QFComponents.QUALITY_DATA_COMPONENT, quality);
        return true;
    }

    public static void applyQuality(final ItemStack stack, @NotNull final Quality quality, @NotNull final BlockState state, @Nullable final Player player, @Nullable final BlockState farmland) {
        if (farmland == null) {
            applyQuality(stack, quality, state, player);
            return;
        }

        double farmlandMultiplier = ServerConfig.getFarmlandMultiplier(state, farmland);

        if (farmlandMultiplier == -1) {
            applyQuality(stack, quality, state, player);
        } else {
            Bonus farmlandBonus = Bonus.multiplicative((float) farmlandMultiplier);
            List<Bonus> bonusList = new ArrayList<>();
            bonusList.add(farmlandBonus);
            applyQuality(stack, quality, state, player, bonusList);
        }
    }

    public static void applyQuality(final ItemStack stack, @NotNull final Quality quality, @NotNull final BlockState state, @Nullable final Player player) {
        applyQuality(stack, quality, state, player, new ArrayList<>());
    }

    public static void applyQuality(final ItemStack stack, @NotNull final Quality quality, @NotNull final BlockState state, @Nullable final Player player, @NotNull final List<Bonus> bonusList) {
        if (isRelevantCrop(state)) {
            float targetChance = ServerConfig.CROP_TARGET_CHANCE.get().floatValue();

            if (stack.is(Tags.Items.SEEDS)) {
                targetChance *= ServerConfig.SEED_CHANCE_MULTIPLIER.get();
            }

            if (targetChance > 0 && quality.level() > 0) {
                bonusList.add(Bonus.multiplicative((float) (targetChance / quality.getType().chance())));
            }

            QualityUtils.applyQuality(stack, player, bonusList);
        } else if (QualityUtils.isValidQuality(quality)) {
            QualityUtils.applyQuality(stack, quality);
        } else if (quality != Quality.PLAYER_PLACED) {
            QualityUtils.applyQuality(stack, player);
        }
    }

    private static boolean isRelevantCrop(final BlockState state) {
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            return true;
        }

        if (Compat.isModLoaded(Compat.FARMERSDELIGHT) && state.getBlock() instanceof WildCropBlock) {
            return true;
        }

        if (Compat.isModLoaded(Compat.FARM_AND_CHARM) && state.is(TagKey.create(Registries.BLOCK, Compat.farmandcharm("wild_crops")))) {
            return true;
        }

        return false;
    }

    public static void handleConversion(@NotNull final ItemStack result, @NotNull final Container container, @Nullable final RecipeHolder<?> recipe) {
        boolean isRecipe = ServerConfig.isRetainQualityRecipe(recipe);
        boolean handleCompacting = ServerConfig.HANDLE_COMPACTING.get();

        if (!isRecipe && !handleCompacting) {
            return;
        }

        Pair<HashMap<Item, Integer>, HashMap<Integer, Integer>> data = getContainerData(container);

        int relevantItemCount = data.getFirst().entrySet().stream().mapToInt(entry -> {
            if (Utils.isValidItem(entry.getKey().getDefaultInstance())) {
                return entry.getValue();
            }

            return 0;
        }).sum();

        Quality quality = getQuality(data.getSecond(), relevantItemCount, result);

        if (quality.level() > 0 && (isRecipe || (getCompactingSize(data.getFirst(), container) == relevantItemCount || /* decompacting */ relevantItemCount == 1 && (result.getCount() == 4 || result.getCount() == 9)))) {
            QualityUtils.applyQuality(result, quality);
        }
    }

    public static boolean isInvalidItem(final ItemStack stack) {
        return hasQuality(stack) || !Utils.isValidItem(stack);
    }

    private static Pair<HashMap<Item, Integer>, HashMap<Integer, Integer>> getContainerData(final Container container) {
        // Collect the amount of qualities present for all items in the container
        HashMap<Integer, Integer> qualities = new HashMap<>();
        HashMap<Item, Integer> items = new HashMap<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack containerStack = container.getItem(i);
            Item item = containerStack.getItem();
            items.put(item, items.getOrDefault(item, 0) + 1);

            if (!Utils.isValidItem(containerStack)) {
                continue;
            }

            Quality quality = QualityUtils.getQuality(containerStack);
            qualities.compute(quality.level(), (key, value) -> value == null ? 1 : value + 1);
        }

        return Pair.of(items, qualities);
    }

    /** Get the most fitting quality (if all items are diamond -> diamond / if 3 are diamond and 6 are gold -> gold) */
    private static Quality getQuality(final HashMap<Integer, Integer> qualities, int itemCount, final ItemStack result) {
        List<Integer> levels = qualities.keySet().stream().sorted(Comparator.comparingInt(Integer::intValue).reversed()).toList();

        for (Integer level : levels) {
            itemCount -= qualities.get(level);

            if (itemCount <= 0) {
                // FIXME 1.21 :: if there are not multiple don't re-create (would constantly randomize effects)
                return Quality.getRandom(result, level);
            }
        }

        return Quality.NONE;
    }

    private static int getCompactingSize(final HashMap<Item, Integer> items, final Container container) {
        Set<Item> keys = items.keySet();

        if (keys.size() != 1 && !(keys.size() == 2 && keys.contains(Items.AIR))) {
            // Either the crafting container only contains 1 type of item or it contains 2 and the other item is air (i.e. no item)
            return -1;
        }

        int containerSize = container.getContainerSize();
        int result = -1;

        for (Item key : keys) {
            int itemCount = items.get(key);

            if (key == Items.AIR && (containerSize - itemCount - /* 2x2 */ 4 != 0 && containerSize - itemCount - /* 3x3 */ 9 != 0)) {
                // If the other slots (besides 2x2 / 3x3) are not empty then it's not a valid compacting recipe
                return -1;
            } else if (key != Items.AIR && (itemCount == /* 2x2 */ 4 || itemCount == /* 3x3 */ 9)) {
                result = itemCount;
            }
        }

        return result;
    }

    /** Returns the {@link Quality} if present, otherwise {@link Quality#NONE} */
    public static Quality getQuality(@Nullable final ItemStack stack) {
        if (stack == null) {
            return Quality.NONE;
        }

        Quality quality = stack.get(QFComponents.QUALITY_DATA_COMPONENT);

        if (quality == null) {
            return Quality.NONE;
        }

        return quality;
    }

    /** Returns the corresponding {@link QualityType} to the {@link Quality} if possible, otherwise {@link QualityType#NONE} */
    public static QualityType getType(final ItemStack stack) {
        return QualityUtils.getQuality(stack).getType();
    }

    public static boolean isValidQuality(final Quality quality) {
        return quality != null && quality.level() > 0;
    }

    public static int countIngredients(final CraftingContainer container) {
        int count = 0;

        for (ItemStack stack : container.getItems()) {
            if (Utils.isValidItem(stack)) {
                count++;
            }
        }

        return count;
    }
}
