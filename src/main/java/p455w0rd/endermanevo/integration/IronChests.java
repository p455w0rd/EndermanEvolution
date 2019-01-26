package p455w0rd.endermanevo.integration;

import cpw.mods.ironchest.common.blocks.chest.BlockIronChest;
import cpw.mods.ironchest.common.blocks.chest.IronChestType;
import cpw.mods.ironchest.common.blocks.shulker.BlockIronShulkerBox;
import cpw.mods.ironchest.common.blocks.shulker.IronShulkerBoxType;
import cpw.mods.ironchest.common.core.IronChestBlocks;
import cpw.mods.ironchest.common.items.chest.ItemIronChest;
import cpw.mods.ironchest.common.items.shulker.ItemIronShulkerBox;
import cpw.mods.ironchest.common.lib.BlockLists;
import cpw.mods.ironchest.common.tileentity.chest.TileEntityIronChest;
import cpw.mods.ironchest.common.tileentity.shulker.TileEntityIronShulkerBox;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelShulker;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.*;
import p455w0rd.endermanevo.init.ModIntegration.Mods;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class IronChests {

	public static IInventory getChestInventory(IronChestType type) {
		TileEntityIronChest chestTile = new TileEntityIronChest();
		ReflectionHelper.setPrivateValue(TileEntityIronChest.class, chestTile, type, "chestType");
		return chestTile;
	}

	public static IInventory getShulkerInventory(IronShulkerBoxType type) {
		TileEntityIronShulkerBox shulkerBoxTile = new TileEntityIronShulkerBox();
		ReflectionHelper.setPrivateValue(TileEntityIronShulkerBox.class, shulkerBoxTile, type, "shulkerBoxType");
		return shulkerBoxTile;
	}

	public static String getShulkerColorName(ItemStack stack) {
		if (stack.getItem() instanceof ItemIronShulkerBox) {
			return ReflectionHelper.getPrivateValue(ItemIronShulkerBox.class, (ItemIronShulkerBox) stack.getItem(), "colorName");
		}
		return "white";
	}

	public static boolean isIronChest(ItemStack stack) {
		return Mods.IRONCHESTS.isLoaded() && stack.getItem() instanceof ItemIronChest;
	}

	public static boolean isIronShulkerBox(ItemStack stack) {
		return Mods.IRONCHESTS.isLoaded() && stack.getItem() instanceof ItemIronShulkerBox;
	}

	public static boolean isIronChest(Block block) {
		return block instanceof BlockIronChest;
	}

	public static boolean isIronShulkerBox(Block block) {
		return block instanceof BlockIronShulkerBox;
	}

	public static boolean canPlayerSilkHarvestChest(TileEntity te, EntityPlayer player) {
		if (!Mods.IRONCHESTS.isLoaded()) {
			return false;
		}
		BlockPos pos = te.getPos();
		Block block = te.getWorld() == null ? null : te.getWorld().getBlockState(pos).getBlock();
		return te != null && block != null && isIronChest(block) && player != null && player.getHeldItemMainhand() != null && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, player.getHeldItemMainhand()) > 0 && !player.isCreative();
	}

	public static ItemStack getStackByMeta(int meta) {
		return new ItemStack(Item.getItemFromBlock(IronChestBlocks.ironChestBlock), 1, meta);
	}

	public static ItemStack getShulkerStackByMeta(int meta) {
		return new ItemStack(Item.getItemFromBlock(BlockLists.SHULKER_BLOCKS.get(meta)));
	}

	public static ItemStack getStackForType(IronChestType type) {
		return getStackByMeta(type.ordinal());
	}

	public static ItemStack getStackWithInventory(TileEntity te) {
		if (Mods.IRONCHESTS.isLoaded() && te != null && te.getWorld() != null && te.getWorld().getBlockState(te.getPos()) != null) {
			IronChestType type = ReflectionHelper.getPrivateValue(TileEntityIronChest.class, (TileEntityIronChest) te, "chestType");
			Item block = Item.getItemFromBlock(te.getWorld().getBlockState(te.getPos()).getBlock());
			ItemStack itemstack = new ItemStack(block, 1, type.ordinal());
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

	public static IronChestType getChestType(ItemStack stack) {
		if (isIronChest(stack)) {
			return IronChestType.VALUES[stack.getItemDamage()];
		}
		return null;
	}

	public static IronShulkerBoxType getShulkerBoxType(ItemStack stack) {
		if (isIronShulkerBox(stack)) {
			return IronShulkerBoxType.VALUES[stack.getItemDamage()];
		}
		return null;
	}

	public static int getInventorySize(ItemStack stack) {
		return getChestType(stack).size;
	}

	public static int getShulkerBoxInventorySize(ItemStack stack) {
		return getShulkerBoxType(stack).size;
	}

	@SideOnly(Side.CLIENT)
	public static void renderChest(ItemStack stack, float lidAngle) {
		ModelChest modelChest = new ModelChest();
		GlStateManager.color(1, 1, 1, 1);
		RenderUtils.bindTexture(getChestType(stack).modelTexture);
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 1.0, 1.0F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.rotate(2 * 90, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		modelChest.chestLid.rotateAngleX = lidAngle;
		modelChest.renderAll();
		GlStateManager.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	public static void renderShulkerBox(ItemStack stack, float lidProgress) {
		ModelShulker shulkerBoxModel = new ModelShulker();
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		String color = getShulkerColorName(stack);
		RenderUtils.bindTexture(new ResourceLocation("ironchest", "textures/model/shulker/" + color + "/shulker_" + color + "" + getShulkerBoxType(stack).modelTexture));
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(0 + 0.5F, 0 + 1.5F, 0 + 0.5F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.0F, 1.0F, 0.0F);
		GlStateManager.scale(0.9995F, 0.9995F, 0.9995F);
		GlStateManager.translate(0.0F, -1.0F, 0.0F);
		shulkerBoxModel.base.render(0.0625F);
		GlStateManager.translate(0.0F, -lidProgress * 0.5F, 0.0F);
		GlStateManager.rotate(270.0F * lidProgress, 0.0F, 1.0F, 0.0F);
		shulkerBoxModel.lid.render(0.0625F);
		GlStateManager.enableCull();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
