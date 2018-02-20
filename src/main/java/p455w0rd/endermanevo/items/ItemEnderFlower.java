package p455w0rd.endermanevo.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import p455w0rd.endermanevo.blocks.BlockEnderFlower;
import p455w0rd.endermanevo.init.ModBlocks;

/**
 * @author p455w0rd
 *
 */
public class ItemEnderFlower extends ItemBase implements IPlantable {

	public ItemEnderFlower() {
		super("ender_flower");
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemstack = player.getHeldItem(hand);
		IBlockState state = worldIn.getBlockState(pos);
		if (facing == EnumFacing.UP && player.canPlayerEdit(pos.offset(facing), facing, itemstack) && (state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) || (BlockEnderFlower.isValidSoil(state.getBlock()) && worldIn.isAirBlock(pos.up())))) {
			IBlockState placedFlowerState = ModBlocks.ENDER_FLOWER.getDefaultState();
			if (player.capabilities.isCreativeMode) {
				placedFlowerState = placedFlowerState.withProperty(BlockCrops.AGE, Integer.valueOf(7));
			}
			worldIn.setBlockState(pos.up(), placedFlowerState);
			if (player instanceof EntityPlayerMP) {
				CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos.up(), itemstack);
			}

			itemstack.shrink(1);
			return EnumActionResult.SUCCESS;
		}
		else {
			return EnumActionResult.FAIL;
		}
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return ModBlocks.ENDER_FLOWER.getDefaultState();
	}

}
