package de.cadentem.quality_food.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiPredicate;

public class FarmlandConfig {
    public static final int INDEX = 0;
    public static final int CROP = 1;
    public static final int FARMLAND = 2;
    public static final int MULTIPLIER = 3;

    public final BiPredicate<BlockState, BlockState> predicate;
    public final double multiplier;
    public final int index;

    @SuppressWarnings("ConstantConditions")
    public FarmlandConfig(final String config) {
        String[] data = config.split(";");
        index = Integer.parseInt(data[INDEX]);

        if (data[CROP].startsWith("#")) {
            TagKey<Block> testCrop = TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(data[CROP].substring(1)));

            if (data[FARMLAND].startsWith("#")) {
                TagKey<Block> testFarmland = TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(data[FARMLAND].substring(1)));
                predicate = (crop, farmland) -> crop.is(testCrop) && farmland.is(testFarmland);
            } else {
                Block testFarmland = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(data[FARMLAND]));
                predicate = (crop, farmland) -> crop.is(testCrop) && farmland.getBlock() == testFarmland;
            }
        } else {
            Block testCrop = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(data[CROP]));

            if (data[FARMLAND].startsWith("#")) {
                TagKey<Block> testFarmland = TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(data[FARMLAND].substring(1)));
                predicate = (crop, farmland) -> crop.getBlock() == testCrop && farmland.is(testFarmland);
            } else {
                Block testFarmland = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(data[FARMLAND]));
                predicate = (crop, farmland) -> crop.getBlock() == testCrop && farmland.getBlock() == testFarmland;
            }
        }

        multiplier = Double.parseDouble(data[MULTIPLIER]);
    }
}
