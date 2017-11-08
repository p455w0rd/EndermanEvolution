package p455w0rd.endermanevo.init;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.IModelHolder;
import p455w0rd.endermanevo.items.ItemFrienderPearl;
import p455w0rd.endermanevo.items.ItemSkullBase;

public class ModItems {

	private static final List<Item> ITEM_LIST = new ArrayList<Item>();

	public static final ItemFrienderPearl FRIENDER_PEARL = new ItemFrienderPearl();
	public static final ItemSkullBase.Enderman SKULL_ENDERMAN = new ItemSkullBase.Enderman();
	public static final ItemSkullBase.Frienderman SKULL_FRIENDERMAN = new ItemSkullBase.Frienderman();
	public static final ItemSkullBase.Enderman2 SKULL_ENDERMAN2 = new ItemSkullBase.Enderman2();

	public static void init() {
		long millis = System.currentTimeMillis() % 1000;
		ITEM_LIST.addAll(Lists.newArrayList(SKULL_ENDERMAN, SKULL_FRIENDERMAN, SKULL_ENDERMAN2, FRIENDER_PEARL));
		ModLogger.info("Registering Items Complete In " + (int) ((System.currentTimeMillis() % 1000) - millis) + "ms");
	}

	@SideOnly(Side.CLIENT)
	public static void preInitModels() {
		ModLogger.info("Init adding item models");
		for (Item item : ITEM_LIST) {
			if (item instanceof IModelHolder) {
				((IModelHolder) item).initModel();
			}
			ModLogger.info(" Registered Model for " + item.getItemStackDisplayName(new ItemStack(item)));
		}
		ModLogger.info("Finished adding item models");
	}

	public static List<Item> getList() {
		return ITEM_LIST;
	}
}
