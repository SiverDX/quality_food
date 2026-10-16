package de.cadentem.quality_food.compat.jade;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.attachments.AnimalData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public class QualityMobDataProvider implements IServerDataProvider<EntityAccessor> {
    @Override
    public Identifier getUid() {
        return QualityFood.location("quality_mob");
    }

    @Override
    public void appendServerData(final CompoundTag tag, final EntityAccessor accessor) {
        AnimalData data = AnimalData.get(accessor.getEntity());

        if (data != null) {
            tag.putDouble(QualityFood.concat("potential"), data.potential);
        }
    }

    @Override
    public boolean shouldRequestData(final EntityAccessor accessor) {
        return accessor.getEntity() instanceof Mob;
    }
}
