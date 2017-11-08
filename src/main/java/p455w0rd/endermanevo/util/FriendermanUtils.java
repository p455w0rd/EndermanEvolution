package p455w0rd.endermanevo.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import p455w0rd.endermanevo.entity.EntityFrienderman;

/**
 * @author p455w0rd
 *
 */
public class FriendermanUtils {

	@SuppressWarnings("unchecked")
	public static List<EntityFrienderman> getTamedFriendermenInRange(EntityPlayer player, double radius) {
		List<EntityFrienderman> friendermenInRange = Lists.<EntityFrienderman>newArrayList();
		List<EntityFrienderman> possibilities = (List<EntityFrienderman>) getEntitiesInRange(EntityFrienderman.class, player.getEntityWorld(), player.posX, player.posY, player.posZ, radius);
		for (EntityFrienderman frienderman : possibilities) {
			if (frienderman.isTamed()) {
				if (frienderman.getOwnerId().equals(player.getUniqueID())) {
					friendermenInRange.add(frienderman);
				}
			}
		}
		return friendermenInRange;
	}

	public static List<? extends Entity> getEntitiesInRange(Class<? extends Entity> entityType, World world, double x, double y, double z, double radius) {
		return world.getEntitiesWithinAABB(entityType, new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius));
	}

	public static List<EntityFrienderman> getTamedFriendermenWithChestInRange(EntityPlayer player, double radius) {
		List<EntityFrienderman> tamedFriendermen = Lists.<EntityFrienderman>newArrayList();
		List<EntityFrienderman> endermanInRadius = Lists.<EntityFrienderman>newArrayList(getTamedFriendermenInRange(player, radius));
		for (EntityFrienderman frienderman : endermanInRadius) {
			if (frienderman.isHoldingChest()) {
				tamedFriendermen.add(frienderman);
			}
		}
		return tamedFriendermen;
	}

	public static IInventory getInventoryForHeldChest(EntityFrienderman frienderman) {
		if (frienderman.isHoldingChest()) {
			return frienderman.getHeldChestInventory();
		}
		return null;
	}

	public static List<EntityFrienderman> getFriendermenWithStorageSpaceInRangeForStack(EntityPlayer player, ItemStack stack, double radius) {
		List<EntityFrienderman> friendermanWithStorageSpaceForStack = Lists.<EntityFrienderman>newArrayList();
		List<EntityFrienderman> endermanInRadius = Lists.<EntityFrienderman>newArrayList(getTamedFriendermenInRange(player, radius));
		for (EntityFrienderman frienderman : endermanInRadius) {
			if (canInsertStack(frienderman.getHeldChestInventory(), stack)) {
				friendermanWithStorageSpaceForStack.add(frienderman);
			}
		}
		return friendermanWithStorageSpaceForStack;
	}

	private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
		return !inventoryIn.isItemValidForSlot(index, stack) ? false : !(inventoryIn instanceof ISidedInventory) || ((ISidedInventory) inventoryIn).canInsertItem(index, stack, side);
	}

	public static boolean canCombine(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() != stack2.getItem() ? false : (stack1.getMetadata() != stack2.getMetadata() ? false : (stack1.getCount() > stack1.getMaxStackSize() ? false : ItemStack.areItemStackTagsEqual(stack1, stack2)));
	}

	public static boolean canInsertStack(IInventory inventoryIn, ItemStack stackIn, int index, EnumFacing side) {
		boolean flag = false;
		ItemStack itemstack = inventoryIn.getStackInSlot(index);
		if (itemstack == null) {
			flag = true;
		}
		else {
			ItemStack stack = stackIn.copy();
			if (canInsertItemInSlot(inventoryIn, stack, index, side)) {
				if (canCombine(itemstack, stack)) {
					//Forge: BUGFIX: Again, make things respect max stack sizes.
					int max = Math.min(stack.getMaxStackSize(), inventoryIn.getInventoryStackLimit());
					if (max > itemstack.getCount()) {
						int i = max - itemstack.getCount();
						int j = Math.min(stack.getCount(), i);
						stack.shrink(j);
						itemstack.grow(j);
						flag = j > 0;
					}
				}

			}
		}
		return flag;
	}

	public static boolean canInsertStack(IInventory inventory, ItemStack stack) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (canInsertStack(inventory, stack, i, null)) {
				return true;
			}
		}
		return false;
	}

	public static EntityFrienderman getFirstFriendermenWithStorageSpaceInRangeForStack(EntityPlayer player, ItemStack stack, double radius) {
		List<EntityFrienderman> endermanInRadius = Lists.<EntityFrienderman>newArrayList(getTamedFriendermenWithChestInRange(player, radius));
		for (EntityFrienderman frienderman : endermanInRadius) {
			if (frienderman.getHeldChestInventory() != null && canInsertStack(frienderman.getHeldChestInventory(), stack)) {
				return frienderman;
			}
		}
		return null;
	}

	public static void setEntityWorld(Entity entity, World world) {
		entity.setWorld(world);
	}

	public static enum ChestType {
			VANILLA, IRONCHEST, ENDERSTORAGE;
	}

}
