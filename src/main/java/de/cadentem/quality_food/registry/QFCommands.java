package de.cadentem.quality_food.registry;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.commands.QualityArgument;
import de.cadentem.quality_food.core.commands.QualityItemArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class QFCommands {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENTS = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, QualityFood.MODID);

    public static final RegistryObject<ArgumentTypeInfo<?, ?>> QUALITY = COMMAND_ARGUMENTS.register("quality", () -> ArgumentTypeInfos.registerByClass(QualityArgument.class, SingletonArgumentInfo.contextFree(QualityArgument::new)));
    public static final RegistryObject<ArgumentTypeInfo<?, ?>> ITEM = COMMAND_ARGUMENTS.register("item", () -> ArgumentTypeInfos.registerByClass(QualityItemArgument.class, SingletonArgumentInfo.contextAware(QualityItemArgument::item)));
}
