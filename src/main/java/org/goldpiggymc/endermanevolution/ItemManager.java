package org.goldpiggymc.endermanevolution;

import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class ItemManager implements ItemRegistryContainer {
    public static final Item FRIENDER_PEARL = new Item(new OwoItemSettings().group(Vars.ITEM_GROUP));
    public static final Item ENDER_FRAGMENT = new Item(new OwoItemSettings().group(Vars.ITEM_GROUP));
}
