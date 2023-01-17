package org.goldpiggymc.endermanevolution;

import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.goldpiggymc.endermanevolution.entity.EntityManager;
import org.goldpiggymc.endermanevolution.entity.custom.EvolvedEnderman;
import org.goldpiggymc.endermanevolution.entity.custom.FriendermanEntity;
import software.bernie.geckolib3.GeckoLib;

public class EndermanEvo implements ModInitializer {

    @Override
    public void onInitialize() {
        Vars.ITEM_GROUP.initialize();
        GeckoLib.initialize();

        FabricDefaultAttributeRegistry.register(EntityManager.FRIENDERMAN, FriendermanEntity.createEndermanAttributes());
        FabricDefaultAttributeRegistry.register(EntityManager.EVOLVED_ENDERMAN, EvolvedEnderman.createEndermanAttributes());

        FieldRegistrationHandler.register(
            ItemManager.class,
            Vars.MOD_ID,
            false
        );
        FieldRegistrationHandler.register(
            BlockManager.class,
            Vars.MOD_ID,
            false
        );
    }
}
