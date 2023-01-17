package org.goldpiggymc.endermanevolution;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import org.goldpiggymc.endermanevolution.entity.EntityManager;

public class ItemManager implements ItemRegistryContainer {

    public static final Item FRIENDER_PEARL = new Item(
        new OwoItemSettings().group(Vars.ITEM_GROUP)
    );
    public static final Item ENDER_FRAGMENT = new Item(
        new OwoItemSettings().group(Vars.ITEM_GROUP)
    );
    public static final Item ENDER_FLOWER_SEED = new AliasedBlockItem(
        BlockManager.ENDER_FLOWER,
        new OwoItemSettings().group(Vars.ITEM_GROUP)
    );

    public static final Item FRIENDERMAN_SPAWN_EGG = new SpawnEggItem(
        EntityManager.FRIENDERMAN,
        0xFB42A6,
        0xF4C9E1,
        new OwoItemSettings().group(Vars.ITEM_GROUP)
    );

    public static final Item EVOLVED_ENDERMAN_SPAWN_EGG = new SpawnEggItem(
            EntityManager.EVOLVED_ENDERMAN,
            0x000000,
            0x048236,
            new OwoItemSettings().group(Vars.ITEM_GROUP)
    );
}
