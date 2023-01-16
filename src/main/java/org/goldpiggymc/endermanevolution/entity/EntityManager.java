package org.goldpiggymc.endermanevolution.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.goldpiggymc.endermanevolution.Vars;
import org.goldpiggymc.endermanevolution.entity.custom.FriendermanEntity;

public class EntityManager {

    public static final EntityType<FriendermanEntity> FRIENDERMAN = Registry.register(
        Registry.ENTITY_TYPE,
        new Identifier(Vars.MOD_ID, "frienderman"),
        FabricEntityTypeBuilder
            .create(SpawnGroup.CREATURE, FriendermanEntity::new)
            .dimensions(EntityDimensions.fixed(0.4f, 0.4f))
            .build()
    );
}
