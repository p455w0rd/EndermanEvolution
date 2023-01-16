package org.goldpiggymc.endermanevolution;

import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;

public class EndermanEvo implements ModInitializer {

    @Override
    public void onInitialize() {
        Vars.ITEM_GROUP.initialize();

        FieldRegistrationHandler.register(ItemManager.class, Vars.MOD_ID, false);
        FieldRegistrationHandler.register(BlockManager.class, Vars.MOD_ID, false);
    }
}