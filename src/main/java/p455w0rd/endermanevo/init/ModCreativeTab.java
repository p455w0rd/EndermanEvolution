package p455w0rd.endermanevo.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
		items.add(new ItemStack(ModItems.ENDER_FLOWER));
		items.add(new ItemStack(ModItems.ENDER_FRAGMENT));

		ItemStack evolvedEndermanEgg = new ItemStack(Items.SPAWN_EGG);
		NBTTagCompound evolvedEndermanTag = new NBTTagCompound();
		NBTTagCompound evolvedEndermanEntityTag = new NBTTagCompound();
		evolvedEndermanEntityTag.setString("id", ModGlobals.MODID + ":enderman_evolved");
		evolvedEndermanTag.setTag("EntityTag", evolvedEndermanEntityTag);
		evolvedEndermanEgg.setTagCompound(evolvedEndermanTag);
		items.add(evolvedEndermanEgg);

		ItemStack frindermanEgg = new ItemStack(Items.SPAWN_EGG);
		NBTTagCompound frindermanTag = new NBTTagCompound();
		NBTTagCompound frindermanEntityTag = new NBTTagCompound();
		frindermanEntityTag.setString("id", ModGlobals.MODID + ":frienderman");
		frindermanTag.setTag("EntityTag", frindermanEntityTag);
		frindermanEgg.setTagCompound(frindermanTag);
		items.add(frindermanEgg);

		ItemStack evolvedEndermiteEgg = new ItemStack(Items.SPAWN_EGG);
		NBTTagCompound evolvedEndermiteTag = new NBTTagCompound();
		NBTTagCompound evolvedEndermiteEntityTag = new NBTTagCompound();
		evolvedEndermiteEntityTag.setString("id", ModGlobals.MODID + ":evolved_endermite");
		evolvedEndermiteTag.setTag("EntityTag", evolvedEndermiteEntityTag);
		evolvedEndermiteEgg.setTagCompound(evolvedEndermiteTag);
		items.add(evolvedEndermiteEgg);

		super.displayAllRelevantItems(items);
	}

	@Override
	public ItemStack getTabIconItem() {
		return null;
	}

}
