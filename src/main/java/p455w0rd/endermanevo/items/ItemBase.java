package p455w0rd.endermanevo.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.IModelHolder;

public class ItemBase extends Item implements IModelHolder {

	private String name = "";

	public ItemBase(String name) {
		this.name = name;
		setRegistryName(this.name);
		setUnlocalizedName(this.name);
		ForgeRegistries.ITEMS.register(this);
		setMaxStackSize(64);
		setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
