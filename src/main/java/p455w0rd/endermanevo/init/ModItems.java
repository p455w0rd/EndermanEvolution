package p455w0rd.endermanevo.init;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.IModelHolder;
import p455w0rd.endermanevo.items.ItemEnderFlower;
import p455w0rd.endermanevo.items.ItemEnderFragment;
import p455w0rd.endermanevo.items.ItemFrienderPearl;
import p455w0rd.endermanevo.items.ItemSkullBase;

public class ModItems {

	private static final List<Item> ITEM_LIST = new ArrayList<Item>();

	public static final ItemFrienderPearl FRIENDER_PEARL = new ItemFrienderPearl();
	public static final ItemSkullBase.Enderman SKULL_ENDERMAN = new ItemSkullBase.Enderman();
	public static final ItemSkullBase.Frienderman SKULL_FRIENDERMAN = new ItemSkullBase.Frienderman();
	public static final ItemSkullBase.EvolvedEnderman SKULL_EVOLVED_ENDERMAN = new ItemSkullBase.EvolvedEnderman();
	public static final ItemEnderFlower ENDER_FLOWER = new ItemEnderFlower();
	public static final ItemEnderFragment ENDER_FRAGMENT = new ItemEnderFragment();

	@SideOnly(Side.CLIENT)
	public static void preInitModels() {
		for (Item item : ITEM_LIST) {
			if (item instanceof IModelHolder) {
				((IModelHolder) item).initModel();
			}
		}
	}

	public static List<Item> getList() {
		if (ITEM_LIST.isEmpty()) {
			ITEM_LIST.addAll(Lists.newArrayList(FRIENDER_PEARL, SKULL_ENDERMAN, SKULL_FRIENDERMAN, SKULL_EVOLVED_ENDERMAN, ENDER_FLOWER, ENDER_FRAGMENT));
		}
		return ITEM_LIST;
	}
}
