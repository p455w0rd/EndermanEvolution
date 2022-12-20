package org.goldpiggymc.endermanevolution;

import io.wispforest.owo.itemgroup.OwoItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemGroup extends OwoItemGroup {
    protected ItemGroup(Identifier id) {
        super(id);
    }

    @Override
    protected void setup() {

    }
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ItemManager.FRIENDER_PEARL);
    }
}
