package p455w0rd.endermanevo.items;

import net.minecraft.item.Item;
import p455w0rd.endermanevo.api.IModelHolder;

public class ItemBase extends Item implements IModelHolder {

	public ItemBase(final String name) {
		setRegistryName(name);
		setUnlocalizedName(name);
		setMaxStackSize(64);
		setMaxDamage(0);
	}

}
