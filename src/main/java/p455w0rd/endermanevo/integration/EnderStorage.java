package p455w0rd.endermanevo.integration;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.block.BlockEnderStorage;
import codechicken.enderstorage.client.render.tile.RenderTileEnderChest;
import codechicken.enderstorage.item.ItemEnderStorage;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import p455w0rd.endermanevo.init.ModIntegration.Mods;
import p455w0rd.endermanevo.init.ModLogger;

/**
 * @author p455w0rd
 *
 */
public class EnderStorage {

	public static void init() {
		ModLogger.info(Mods.ENDERSTORAGE.getName() + " Integation: Enabled");
		//EntityFrienderman.setCarriable(getEnderStorageBlock(), true);
	}

	public static BlockEnderStorage getEnderStorageBlock() {
		return codechicken.enderstorage.init.ModBlocks.blockEnderStorage;
	}

	public static ItemStack getEnderStorageStack() {
		return new ItemStack(getEnderStorageItem());
	}

	public static ItemEnderStorage getEnderStorageItem() {
		return (ItemEnderStorage) Item.getItemFromBlock(getEnderStorageBlock());
	}

	public static EnderItemStorage getStorageFromItem(World world, ItemStack stack) {
		Frequency frequency = Frequency.readFromStack(stack);
		return ((EnderItemStorage) EnderStorageManager.instance(world.isRemote).getStorage(frequency, "item"));
	}

	public static IInventory getInventoryFromStorage(EnderItemStorage storage) {
		return storage;
	}

	public static void renderItemChest(ItemStack stack, float lidAngle) {
		Frequency frequency = Frequency.readFromStack(stack);
		RenderTileEnderChest.renderChest(2, frequency, 0, 0, 0, 0, lidAngle);
	}

}
