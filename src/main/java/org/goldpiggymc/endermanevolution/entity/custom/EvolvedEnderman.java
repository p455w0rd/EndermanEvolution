package org.goldpiggymc.endermanevolution.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.world.World;

public class EvolvedEnderman extends EndermanEntity {

    public EvolvedEnderman(
        EntityType<? extends EndermanEntity> entityType,
        World world
    ) {
        super(entityType, world);
    }
}
