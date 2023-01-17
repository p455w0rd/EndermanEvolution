package org.goldpiggymc.endermanevolution.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FriendermanEntity extends EndermanEntity {
    public FriendermanEntity(EntityType<? extends EndermanEntity> entityType, World world) {
        super(entityType, world);
    }
}
