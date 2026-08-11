package de.cadentem.quality_food.compat.jade;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.capability.AnimalDataProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.text.NumberFormat;

public class QualityMobProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    private static final ResourceLocation ID = new ResourceLocation(QualityFood.MODID, "quality_mob");

    @Override
    public void appendTooltip(final ITooltip toolTip, final EntityAccessor accessor, final IPluginConfig config) {
        if (!(accessor.getEntity() instanceof Mob)) {
            return;
        }

        double potential = accessor.getServerData().getDouble("quality_food.potential");

        if (potential == 0) {
            return;
        }

        toolTip.add(Component.translatable("quality_food.potential", NumberFormat.getPercentInstance().format(potential)));
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public void appendServerData(final CompoundTag tag, final EntityAccessor accessor) {
        if (!(accessor.getEntity() instanceof Mob mob)) {
            return;
        }

        AnimalDataProvider.getCapability(mob).ifPresent(data -> tag.putDouble("quality_food.potential", data.potential));
    }
}