/*
 * This file is part of Wireless Crafting Terminal. Copyright (c) 2017, p455w0rd
 * (aka TheRealp455w0rd), All rights reserved unless otherwise stated.
 *
 * Wireless Crafting Terminal is free software: you can redistribute it and/or
 * modify it under the terms of the MIT License.
 *
 * Wireless Crafting Terminal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the MIT License for
 * more details.
 *
 * You should have received a copy of the MIT License along with Wireless
 * Crafting Terminal. If not, see <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.endermanevo.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author p455w0rd
 *
 */
public class ChestUtils {

	private static List<BlockShulkerBox> SHULKER_BOXES = Lists.<BlockShulkerBox>newArrayList();

	public static List<BlockShulkerBox> getShulkerBoxes() {
		if (SHULKER_BOXES.isEmpty()) {
			for (EnumDyeColor color : EnumDyeColor.values()) {
				SHULKER_BOXES.add((BlockShulkerBox) BlockShulkerBox.getBlockByColor(color));
			}
		}
		return SHULKER_BOXES;
	}

	public static boolean isVanillaChest(ItemStack stack) {
		boolean containsPathString = false;
		for (int i = 0; i < VanillaChestTypes.values().length; i++) {
			if (stack.getItem().getRegistryName().getResourcePath().toString().contains(VanillaChestTypes.values()[i].getName())) {
				containsPathString = true;
				break;
			}
		}
		return stack.getItem().getRegistryName().getResourceDomain().toString().equals("minecraft") && containsPathString;
	}

	public static boolean isVanillaChest(Block block) {
		return isVanillaChest(block, true);
	}

	public static boolean isVanillaChest(Block block, boolean includeEnderChest) {
		return block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || (includeEnderChest ? block == Blocks.ENDER_CHEST : false);
	}

	public static boolean isVanillaShulkerBox(ItemStack stack) {
		return stack.getItem().getRegistryName().getResourceDomain().toString().equals("minecraft") && stack.getItem() instanceof ItemShulkerBox;
	}

	public static boolean isVanillaShulkerBox(Block block) {
		return block instanceof BlockShulkerBox && getShulkerBoxes().contains(block);
	}

	public static VanillaChestTypes getVanillaChestType(ItemStack stack) {
		if (isVanillaChest(stack)) {
			for (int i = 0; i < VanillaChestTypes.values().length; i++) {
				if (stack.getItem().getRegistryName().getResourcePath().toString().equals(VanillaChestTypes.values()[i].getName())) {
					return VanillaChestTypes.values()[i];
				}
			}
		}
		return null;
	}

	public static IInventory loadInventoryFromStack(IInventory inventory, ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
			NBTTagCompound tag = stack.getSubCompound("BlockEntityTag");
			if (tag.hasKey("Items")) {
				NBTTagList list = tag.getTagList("Items", 10);
				if (list.tagCount() > 0) {
					for (int i = 0; i < list.tagCount(); i++) {
						inventory.setInventorySlotContents(i, new ItemStack(list.getCompoundTagAt(i)));
					}
				}
			}
		}
		return null;
	}

	public static ItemStack getStackWithInventory(IInventory inventory, ItemStack stack) {
		if (inventory != null && inventory.getSizeInventory() > 0 && stack != null) {
			NBTTagCompound stackNBT = stack.getOrCreateSubCompound("BlockEntityTag");
			NBTTagList stackList = new NBTTagList();
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				NBTTagCompound slotNBT = new NBTTagCompound();
				slotNBT.setByte("Slot", (byte) i);
				inventory.getStackInSlot(i).writeToNBT(slotNBT);
				stackList.appendTag(slotNBT);
			}
			stackNBT.setTag("Items", stackList);
			stack.setTagInfo("BlockEntityTag", stackNBT);
			return stack;
		}
		return null;
	}

	public static ItemStack getStackWithInventory(IInventory inventory, IBlockState state) {
		if (inventory != null && inventory.getSizeInventory() > 0 && state != null) {
			ItemStack stack = new ItemStack(Item.getItemFromBlock(state.getBlock()));
			NBTTagCompound stackNBT = new NBTTagCompound();//stack.getSubCompound("BlockEntityTag", true);
			NBTTagList stackList = new NBTTagList();
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (inventory.getStackInSlot(i) == null) {
					continue;
				}
				NBTTagCompound slotNBT = new NBTTagCompound();
				slotNBT.setByte("Slot", (byte) i);
				inventory.getStackInSlot(i).writeToNBT(slotNBT);
				stackList.appendTag(slotNBT);
			}
			stackNBT.setTag("Items", stackList);
			stack.setTagInfo("BlockEntityTag", stackNBT);
			return stack;
		}
		return null;
	}

	public static ItemStack getStackWithInventory(TileEntity te) {
		if (te != null && te.getWorld() != null && te.getWorld().getBlockState(te.getPos()) != null) {
			ItemStack itemstack = new ItemStack(Item.getItemFromBlock(te.getWorld().getBlockState(te.getPos()).getBlock()));
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			te.writeToNBT(nbttagcompound);
			itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
			return itemstack;
		}
		return null;
	}

	public static EntityItem getEntityItemFromBlockWithInventory(World world, BlockPos pos) {
		if (world != null && world.getBlockState(pos) != null && world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof IInventory) {
			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();
			return new EntityItem(world, x, y + 1, z, getStackWithInventory(world.getTileEntity(pos)));
		}
		return null;
	}

	public static boolean canPlayerSilkHarvestChest(TileEntity te, EntityPlayer player) {
		BlockPos pos = te.getPos();
		Block block = te.getWorld() == null ? null : te.getWorld().getBlockState(pos).getBlock();
		return te != null && block != null && ChestUtils.isVanillaChest(block, false) && player != null && player.getHeldItemMainhand() != null && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand()) > 0 && !player.isCreative();
	}

	public static enum VanillaChestTypes implements IStringSerializable {
			NORMAL("chest"), TRAPPED("trapped_chest"), ENDER("ender_chest");

		String resourceString;

		VanillaChestTypes(String path) {
			resourceString = path;
		}

		@Override
		public String getName() {
			return resourceString;
		}
	}

}
