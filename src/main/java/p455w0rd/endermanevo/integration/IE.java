package p455w0rd.endermanevo.integration;

import java.util.HashSet;

import com.google.common.collect.Sets;

import blusunrize.immersiveengineering.api.ComparableItemStack;
import blusunrize.immersiveengineering.api.tool.BelljarHandler;
import blusunrize.immersiveengineering.api.tool.BelljarHandler.DefaultPlantHandler;
import blusunrize.immersiveengineering.common.blocks.metal.TileEntityBelljar;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.blocks.BlockEnderFlower;
import p455w0rd.endermanevo.init.ModItems;

/**
 * @author p455w0rd
 *
 */
public class IE {

	private static final DefaultPlantHandler ENDERFLOWER_HANDLER = new DefaultPlantHandler() {

		private final HashSet<ComparableItemStack> validSeeds = Sets.newHashSet(new ComparableItemStack(new ItemStack(ModItems.ENDER_FLOWER), false, false));

		@Override
		protected HashSet<ComparableItemStack> getSeedSet() {
			return validSeeds;
		}

		@Override
		public boolean isValid(final ItemStack seed) {
			return seed != null && !seed.isEmpty() && seed.getItem() == ModItems.ENDER_FLOWER;
		}

		@Override
		public boolean isCorrectSoil(final ItemStack seed, final ItemStack soil) {
			for (final ItemStack validSoil : BlockEnderFlower.VALID_SOILS_STACKS) {
				if (validSoil.getItem() == soil.getItem()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public float getGrowthStep(final ItemStack seed, final ItemStack soil, final float growth, final TileEntity tile, final float fertilizer, final boolean render) {
			boolean isBonemealable = false;
			for (final Block boneMealSoil : BlockEnderFlower.VALID_BONEMEAL_SOILS) {
				if (boneMealSoil == Block.getBlockFromItem(soil.getItem())) {
					isBonemealable = true;
					break;
				}
			}
			return .003125f * (isBonemealable ? fertilizer * 2 : 0.25f);
		}

		@Override
		public ItemStack[] getOutput(final ItemStack seed, final ItemStack soil, final TileEntity tile) {
			if (tile instanceof TileEntityBelljar) {
				final TileEntityBelljar bellJar = (TileEntityBelljar) tile;
				final int age = getGrowth(bellJar);
				if (age == 1) {
					return new ItemStack[] {
							new ItemStack(ModItems.ENDER_FRAGMENT)
					};
				}
			}
			return new ItemStack[] {
					ItemStack.EMPTY
			};
		}

		private int getGrowth(final TileEntityBelljar tile) {
			final NBTTagCompound nbt = new NBTTagCompound();
			tile.writeCustomNBT(nbt, false);
			if (nbt.hasKey("growth", NBT.TAG_FLOAT)) {
				return Math.round(nbt.getFloat("growth"));
			}
			return 0;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public IBlockState[] getRenderedPlant(final ItemStack seed, final ItemStack soil, final float growth, final TileEntity tile) {
			return new IBlockState[] {
					BlockEnderFlower.getGrowthStates()[Math.min(7, Math.round(7 * growth))]
			};
		}

	};

	public static void registerClocheRecipe() {
		BelljarHandler.registerHandler(ENDERFLOWER_HANDLER);
	}

}
