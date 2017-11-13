package p455w0rd.endermanevo.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ModCreativeTab extends CreativeTabs {

	public static CreativeTabs TAB;

	public static void init() {
		TAB = new ModCreativeTab();
	}

	public ModCreativeTab() {
		super(ModGlobals.MODID);
	}

	@Override
	public ItemStack getIconItemStack() {
		return new ItemStack(ModItems.SKULL_ENDERMAN);
	}

	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> items) {
		items.add(new ItemStack(ModItems.FRIENDER_PEARL));
		items.add(new ItemStack(ModItems.SKULL_ENDERMAN));
		items.add(new ItemStack(ModItems.SKULL_FRIENDERMAN));
		items.add(new ItemStack(ModItems.SKULL_EVOLVED_ENDERMAN));
		super.displayAllRelevantItems(items);
	}

	@Override
	public ItemStack getTabIconItem() {
		return null;
	}

}
